package org.openlyrics.jlyrics.massreader;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.exception.LyricsException;
import org.openlyrics.jlyrics.reader.ILyricsReader;
import org.openlyrics.jlyrics.song.SongVersion;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.lyrics.VerseLine;
import org.openlyrics.jlyrics.song.lyrics.linepart.LineTag;
import org.openlyrics.jlyrics.song.lyrics.linepart.Text;
import org.openlyrics.jlyrics.song.properties.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

public class OpenLPDBReader implements IMassReader {

    private static final String queryAllSongIdsSQL =
            "SELECT id FROM songs ORDER BY id";
    private static final String querySongByIdSQL =
            "SELECT title, alternate_title, lyrics, verse_order, copyright, " +
            "comments, ccli_number, last_modified FROM songs WHERE id = ?";

    private static final String queryAuthorsBySongIdSQL =
            "SELECT authors.display_name, authors_songs.author_type FROM authors " +
            "INNER JOIN authors_songs ON authors.id = authors_songs.author_id " +
            "AND authors_songs.song_id = ?";

    private static final String querySongbooksBySongIdSQL =
            "SELECT song_books.name, songs_songbooks.entry FROM song_books " +
            "INNER JOIN songs_songbooks ON song_books.id = songs_songbooks.songbook_id " +
            "AND songs_songbooks.song_id = ?";

    private static final String queryThemesBySongIdSQL =
            "SELECT topics.name FROM topics " +
            "INNER JOIN songs_topics ON topics.id = songs_topics.topic_id " +
            "AND songs_topics.song_id = ?";

    private final List<Integer> songIds = new ArrayList<>();
    private Integer currentSong;
    private Connection connection;

    @Override
    public IMassReader init(String path) throws Exception {
        this.songIds.clear();
        this.currentSong = -1;

        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        ResultSet resultSet = this.connection.prepareStatement(queryAllSongIdsSQL).executeQuery();
        while (resultSet.next()) {
            songIds.add(resultSet.getInt("id"));
        }

        return this;
    }

    @Override
    public IMassReader init(String path, ILyricsReader reader) throws Exception {
        return this.init(path);
    }

    @Override
    public boolean hasNext() {
        return (currentSong + 1) < songIds.size();
    }

    @Override
    public Song next() {
        if (this.currentSong >= songIds.size()) {
            return null;
        }

        ++this.currentSong;

        try {
            PreparedStatement statement = this.connection.prepareStatement(querySongByIdSQL);
            statement.setInt(1, this.songIds.get(this.currentSong));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Song song = new Song();
                song.setVersion(SongVersion.V0_8);
                song.setCreatedIn("OpenLP");
                song.setModifiedIn("OpenLP");
                song.setModifiedDate(resultSet.getString("last_modified").replace(' ', 'T'));

                song.getProperties().getTitles().clear();
                song.getProperties().getTitles().add(new Title().setTitle(resultSet.getString("title")));

                String alternativeTitle = resultSet.getString("alternate_title");
                if (alternativeTitle != null && !alternativeTitle.isEmpty()) {
                    song.getProperties().getTitles().add(new Title().setTitle(alternativeTitle));
                }

                String verseOrder = resultSet.getString("verse_order");
                if (verseOrder != null && !verseOrder.isEmpty()) {
                    song.getProperties().setVerseOrder(verseOrder);
                }

                String copyright = resultSet.getString("copyright");
                if (copyright != null && !copyright.isEmpty()) {
                    song.getProperties().setCopyright(copyright);
                }

                String comments = resultSet.getString("comments");
                if (comments != null && !comments.isEmpty()) {
                    Arrays.asList(comments.split("\n")).forEach(song.getProperties().getComments()::add);
                }

                String ccliNumber = resultSet.getString("ccli_number");
                if (ccliNumber != null && !ccliNumber.isEmpty()) {
                    song.getProperties().setCcliNo(parseInt(ccliNumber));
                }

                setAuthorsIntoSong(song);
                setSongbooksIntoSong(song);
                setThemesIntoSong(song);
                setLyricsIntoSong(song, resultSet.getString("lyrics"));

                return song;
            }
        }
        //Iterator.next() implementation can't throw checked exception
        catch (SQLException e) {
            throw new LyricsException(e.getMessage());
        }

        return null;
    }

    public void close() throws Exception {
        this.connection.close();
    }

    private void setAuthorsIntoSong(Song song) throws SQLException {
        PreparedStatement authorStatement = this.connection.prepareStatement(queryAuthorsBySongIdSQL);
        authorStatement.setInt(1, this.songIds.get(this.currentSong));
        ResultSet authorSet = authorStatement.executeQuery();

        while (authorSet.next()) {
            Author author = new Author().setName(authorSet.getString("display_name"));
            String authorType = authorSet.getString("author_type");
            if (authorType != null && !authorType.isEmpty()) {
                author.setType(AuthorType.valueOf(authorType.toUpperCase()));
                if (author.getType() == AuthorType.TRANSLATION) {
                    //lang field for song validity, please set to other value manually, not stored in OpenLP
                    author.setLang("en");
                }
            }
            song.getProperties().getAuthors().add(author);
        }
    }

    private void setSongbooksIntoSong(Song song) throws SQLException {
        PreparedStatement songbookStatement = this.connection.prepareStatement(querySongbooksBySongIdSQL);
        songbookStatement.setInt(1, this.songIds.get(this.currentSong));
        ResultSet songbookSet = songbookStatement.executeQuery();

        while (songbookSet.next()) {
            Songbook songbook = new Songbook().setName(songbookSet.getString("name"));
            String entry = songbookSet.getString("entry");
            if (entry != null && !entry.isEmpty()) {
                songbook.setEntry(entry);
            }
            song.getProperties().getSongbooks().add(songbook);
        }
    }

    private void setThemesIntoSong(Song song) throws SQLException {
        PreparedStatement themeStatement = this.connection.prepareStatement(queryThemesBySongIdSQL);
        themeStatement.setInt(1, this.songIds.get(this.currentSong));
        ResultSet themeSet = themeStatement.executeQuery();

        while (themeSet.next()) {
            song.getProperties().getThemes().add(new Theme().setTheme(themeSet.getString("name")));
        }
    }

    private void setLyricsIntoSong(Song song, String xmlLyrics) {
        song.getLyrics().clear();
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(xmlLyrics.getBytes())));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new LyricsException(e.getMessage());
        }

        NodeList verseList = document.getElementsByTagName("verse");
        for (int i = 0; i < verseList.getLength(); i++) {
            Element elVerse = (Element) verseList.item(i);
            String verseName = elVerse.getAttribute("type") + elVerse.getAttribute("label");
            Verse verse = new Verse().setName(verseName);
            VerseLine line = verse.getLines().get(0);
            String[] content = elVerse.getTextContent().split("\n");
            for (int j = 0; j < content.length; j++) {
                line.getParts().add(new Text().setContent(content[j]));
                if (j+1 != content.length) {
                    line.getParts().add(new LineTag().setName("br"));
                }
            }
            song.getLyrics().add(verse);
        }
    }

}
