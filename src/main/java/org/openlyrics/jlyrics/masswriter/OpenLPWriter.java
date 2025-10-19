package org.openlyrics.jlyrics.masswriter;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.lyrics.ILyricsEntry;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.properties.Author;
import org.openlyrics.jlyrics.song.properties.Songbook;
import org.openlyrics.jlyrics.song.properties.Theme;
import org.openlyrics.jlyrics.transform.SongTransformerConfig;
import org.openlyrics.jlyrics.util.SongUtils;
import org.openlyrics.jlyrics.writer.ILyricsWriter;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.openlyrics.jlyrics.util.SongUtils.readAllBytes;

public class OpenLPWriter implements IMassWriter {

    private static final String selectLastInsertIdSQL =
            "SELECT last_insert_rowid() as id";

    private static final String insertSongSQL =
            "INSERT INTO songs (title, alternate_title, lyrics, verse_order, copyright, comments, ccli_number, " +
            "search_title, search_lyrics, create_date, last_modified, temporary) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";

    private static final String selectAuthorByNameSQL =
            "SELECT id AS author_id FROM authors WHERE display_name = ?";

    private static final String insertAuthorSQL =
            "INSERT INTO authors (first_name, last_name, display_name) VALUES (?, ?, ?)";

    private static final String insertSongAuthorSQL =
            "INSERT INTO authors_songs (author_id, song_id, author_type) VALUES (?, ?, ?)";

    private static final String selectSongbookByNameSQL =
            "SELECT id AS songbook_id FROM song_books WHERE name = ?";

    private static final String insertSongbookSQL =
            "INSERT INTO song_books (name) VALUES (?)";

    private static final String insertSongSongbookSQL =
            "INSERT INTO songs_songbooks (songbook_id, song_id, entry) VALUES (?, ?, ?)";

    private static final String selectTopicByNameSQL =
            "SELECT id AS topic_id FROM topics WHERE name = ?";

    private static final String insertTopicSQL =
            "INSERT INTO topics (name) VALUES (?)";

    private static final String insertSongTopicSQL =
            "INSERT INTO songs_topics (song_id, topic_id) VALUES (?, ?)";

    private String path;
    private SongTransformerConfig config;
    private Connection connection;
    int itemCounter;
    int batchCounter;

    @Override
    public String getFileExtension() {
        return ".sqlite";
    }

    @Override
    public IMassWriter init(String path, ILyricsWriter writer, SongTransformerConfig config) throws Exception {
        this.path = path;
        this.config = config;
        this.itemCounter = 0;
        this.batchCounter = 0;

        createNewDatabaseFile();
        return this;
    }

    @Override
    public IMassWriter add(Song song) throws Exception {
        if (config.getBatchSize() > 0 && itemCounter >= config.getBatchSize()) {
            this.connection.close();
            ++batchCounter;
            createNewDatabaseFile();
            itemCounter = 0;
        }

        PreparedStatement statement = this.connection.prepareStatement(insertSongSQL);
        statement.setString(1, song.getProperties().getTitles().get(0).getTitle());

        if (song.getProperties().getTitles().size() > 1) {
            statement.setString(2, song.getProperties().getTitles().get(1).getTitle());
        } else {
            statement.setString(2, "");
        }

        statement.setString(3, this.getLyricsXML(song));

        String verseOrder = song.getProperties().getVerseOrder();
        statement.setString(4, verseOrder != null ? verseOrder: "");

        String copyright = song.getProperties().getCopyright();
        statement.setString(5, copyright != null ? copyright: "");

        if (song.getProperties().getComments() != null) {
            statement.setString(6, String.join("\n", song.getProperties().getComments()));
        } else {
            statement.setString(6, "");
        }

        if (song.getProperties().getCcliNo() != null) {
            statement.setInt(7, song.getProperties().getCcliNo());
        } else {
            statement.setNull(7, Types.INTEGER);
        }

        statement.setString(8, this.getSearchTitle(song));

        statement.setString(9, this.getSearchLyrics(song));

        ZonedDateTime timeToWrite = SongUtils.stringToDate(song.getModifiedDate());
        if (timeToWrite == null) {
            timeToWrite = ZonedDateTime.now();
        }
        String songDate = timeToWrite.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        statement.setString(10, songDate);
        statement.setString(11, songDate);
        statement.execute();

        int songId;
        PreparedStatement lastStatement = connection.prepareStatement(selectLastInsertIdSQL);
        ResultSet lastRS = lastStatement.executeQuery();
        if (lastRS.next()) {
            songId = lastRS.getInt("id");
        } else {
            throw new SQLException("Can't get last inserted row id!");
        }

        this.addAuthors(songId, song);
        this.addSongbooks(songId, song);
        this.addTopics(songId, song);

        ++itemCounter;
        return this;
    }

    private void createNewDatabaseFile() throws Exception {
        String fullPath = path + (batchCounter > 0 ? "-" + batchCounter : "") + getFileExtension();
        File output = new File (fullPath);
        if (output.length() > 0) {
            if (!output.delete()) {
                System.out.println("Az output fájl már létezik, de nem törölhető!");
            }
        }

        this.connection = DriverManager.getConnection("jdbc:sqlite:" + fullPath);
        this.createTables();
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }

    private void createTables() throws Exception {
        try (
            InputStream createStream = this.getClass().getClassLoader().getResourceAsStream("OpenLP.create.sql")
        ) {
            if (createStream != null) {
                String[] createScript = new String(readAllBytes(createStream), StandardCharsets.UTF_8).split(";");
                for (String script : createScript) {
                    this.connection.prepareStatement(script).execute();
                }
            }

        }
    }

    public String getLyricsXML(Song song) throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.setXmlStandalone(true);
        StringWriter stringWriter = new StringWriter();
        stringWriter.append("<?xml version='1.0' encoding='UTF-8'?>\n");

        Element elSong = document.createElement("song");
        elSong.setAttribute("version", "1.0");
        document.appendChild(elSong);

        Element elLyrics = document.createElement("lyrics");
        elSong.appendChild(elLyrics);

        for (ILyricsEntry entry : song.getLyrics()) {
            if (entry instanceof Verse) {
                Verse verse = (Verse) entry;
                Element elVerse = document.createElement("verse");
                elVerse.setAttribute("A__type", verse.getName().substring(0, 1));
                elVerse.setAttribute("B__label", verse.getName().substring(1));
                CDATASection elCData = document.createCDATASection(
                        SongUtils.getVerseTextContent(verse, false).replaceAll("\r", ""));
                elVerse.appendChild(elCData);
                elLyrics.appendChild(elVerse);
            }
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "verse");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));

        return stringWriter.toString().replaceAll("[A-Z]__", "");
    }

    public String getSearchTitle(Song song) {
        return song.getProperties().getTitles().get(0).getTitle().toLowerCase().replaceAll("[.,;?!_\\-]", "") + "@";
    }

    public String getSearchLyrics(Song song) {
        StringBuilder sb = new StringBuilder();

        for (ILyricsEntry entry : song.getLyrics()) {
            if (entry instanceof Verse) {
                Verse verse = (Verse) entry;
                sb.append(SongUtils.getVerseTextContent(verse, false)).append(" ");
            }
        }

        return sb.toString()
                .trim()
                .toLowerCase()
                .replaceAll("\r\n", " ")
                .replaceAll("\n", " ")
                .replaceAll("[.,;?!_\\-]", "")
                .replaceAll(" +", " ");
    }

    public void addAuthors(int songId, Song song) throws SQLException {
        for (Author author : song.getProperties().getAuthors()) {
            int authorId;
            PreparedStatement stmSelect = this.connection.prepareStatement(selectAuthorByNameSQL);
            stmSelect.setString(1, author.getName());
            ResultSet rsSelect = stmSelect.executeQuery();
            if (rsSelect.next()) {
                authorId = rsSelect.getInt("author_id");
            } else {
                List<String> nameParts = this.splitNameToFirstAndLast(author.getName());
                PreparedStatement stmNew = this.connection.prepareStatement(insertAuthorSQL);
                stmNew.setString(1, nameParts.get(0));
                stmNew.setString(2, nameParts.get(1));
                stmNew.setString(3, author.getName());
                stmNew.execute();

                PreparedStatement stmGetId = this.connection.prepareStatement(selectLastInsertIdSQL);
                ResultSet rsGetId = stmGetId.executeQuery();
                if (rsGetId.next()) {
                    authorId = rsGetId.getInt("id");
                } else {
                    throw new SQLException("Can't get last inserted row id!");
                }
            }
            PreparedStatement stmInsert = this.connection.prepareStatement(insertSongAuthorSQL);
            stmInsert.setInt(1, authorId);
            stmInsert.setInt(2, songId);
            stmInsert.setString(3, (author.getType() == null ? "" : author.getType().getName()));
            stmInsert.execute();
        }
    }

    public List<String> splitNameToFirstAndLast(String fullName) {
        List<String> result = new ArrayList<>();
        int spacePos = fullName.indexOf(" ");
        if (spacePos >= 0) {
            result.add(fullName.substring(0, spacePos));
            result.add(fullName.substring(spacePos+1));
        }
        else {
            result.add(fullName);
            result.add("");
        }
        return result;
    }

    public void addSongbooks(int songId, Song song) throws SQLException {
        for (Songbook songbook : song.getProperties().getSongbooks()) {
            int songbookId;
            PreparedStatement stmSelect = this.connection.prepareStatement(selectSongbookByNameSQL);
            stmSelect.setString(1, songbook.getName());
            ResultSet rsSelect = stmSelect.executeQuery();
            if (rsSelect.next()) {
                songbookId = rsSelect.getInt("songbook_id");
            } else {
                PreparedStatement stmNew = this.connection.prepareStatement(insertSongbookSQL);
                stmNew.setString(1, songbook.getName());
                stmNew.execute();

                PreparedStatement stmGetId = this.connection.prepareStatement(selectLastInsertIdSQL);
                ResultSet rsGetId = stmGetId.executeQuery();
                if (rsGetId.next()) {
                    songbookId = rsGetId.getInt("id");
                } else {
                    throw new SQLException("Can't get last inserted row id!");
                }
            }
            PreparedStatement stmInsert = this.connection.prepareStatement(insertSongSongbookSQL);
            stmInsert.setInt(1, songbookId);
            stmInsert.setInt(2, songId);
            stmInsert.setString(3, songbook.getEntry() != null ? songbook.getEntry() : "");
            stmInsert.execute();
        }
    }

    public void addTopics(int songId, Song song) throws SQLException {
        for (Theme theme : song.getProperties().getThemes()) {
            int themeId;
            PreparedStatement stmSelect = this.connection.prepareStatement(selectTopicByNameSQL);
            stmSelect.setString(1, theme.getTheme());
            ResultSet rsSelect = stmSelect.executeQuery();
            if (rsSelect.next()) {
                themeId = rsSelect.getInt("topic_id");
            } else {
                PreparedStatement stmNew = this.connection.prepareStatement(insertTopicSQL);
                stmNew.setString(1, theme.getTheme());
                stmNew.execute();

                PreparedStatement stmGetId = this.connection.prepareStatement(selectLastInsertIdSQL);
                ResultSet rsGetId = stmGetId.executeQuery();
                if (rsGetId.next()) {
                    themeId = rsGetId.getInt("id");
                } else {
                    throw new SQLException("Can't get last inserted row id!");
                }
            }
            PreparedStatement stmInsert = this.connection.prepareStatement(insertSongTopicSQL);
            stmInsert.setInt(1, songId);
            stmInsert.setInt(2, themeId);
            stmInsert.execute();
        }
    }
}
