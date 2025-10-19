package org.openlyrics.jlyrics.util;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.lyrics.ILyricsEntry;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.lyrics.VerseLine;
import org.openlyrics.jlyrics.song.lyrics.linepart.ILinePart;
import org.openlyrics.jlyrics.song.lyrics.linepart.LineTag;
import org.openlyrics.jlyrics.song.lyrics.linepart.Text;
import org.openlyrics.jlyrics.song.properties.Songbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class SongUtils {

    public static String dateToString(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static ZonedDateTime stringToDate(String date) {

        if (date.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}")) {
            return ZonedDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        if (date.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
            LocalDateTime ldt = LocalDateTime.parse(date);
            return ZonedDateTime.ofLocal(ldt, ZoneId.systemDefault(), null);
        }

        if (date.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z")) {
            Instant instant = Instant.parse(date);
            return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        }

        return null;
    }

    public static String getSongTextContent(Song song) {
        return getSongTextContent(song, true);
    }

    public static String getSongTextContent(Song song, boolean appendVerseName) {
        StringBuilder sb = new StringBuilder();

        for (ILyricsEntry entry : song.getLyrics()) {
            if (entry instanceof Verse) {
                Verse verse = (Verse) entry;
                sb.append(getVerseTextContent(verse, appendVerseName)).append("\r\n\r\n");
            }
        }

        return sb.toString().trim();
    }

    public static String getVerseTextContent(Verse verse, boolean appendVerseName) {
        StringBuilder sb = new StringBuilder();
        if (appendVerseName) {
            sb.append(verse.getFormattedName()).append("\r\n");
        }

        for (VerseLine line : verse.getLines()) {
            sb.append(getLineTextContent(line)).append("\r\n");
        }
        return sb.toString().trim();
    }

    public static String getLineTextContent(VerseLine line) {
        //modify text according to inline formatting context
        //(https://developer.mozilla.org/en-US/docs/Web/API/Document_Object_Model/Whitespace)
        //1. all spaces and tabs immediately before and after a line break are ignored
        //4. any space immediately following another space is ignored
        //5. sequences of spaces at the beginning and end of an element are removed
        return getTextFromPartList(line.getParts())
                .replaceAll("\r\n\r\n", "\r\n\n") //EasyWorship workaround for empty line without starting new slide
                .replaceAll((" *\n *"), "\n")
                .replaceAll(" {2,}", " ")
                .trim();
    }

    private static String getTextFromPartList(List<ILinePart> parts) {
        StringBuilder sb = new StringBuilder();

        for (ILinePart part : parts) {
            if (part instanceof Text) {
                Text text = (Text) part;

                //modify text according to inline formatting context
                //(https://developer.mozilla.org/en-US/docs/Web/API/Document_Object_Model/Whitespace)
                //1. all spaces and tabs immediately before and after a line break are ignored
                //2. all tab characters are handled as space characters
                //3. line breaks are converted to spaces
                sb.append(text.getContent().replaceAll(" *\n *", " ").replaceAll("\t", " "));
            } else
            if (part instanceof LineTag) {
                LineTag lineTag = (LineTag) part;
                if (lineTag.getName().equals("br")) {
                    sb.append("\r\n");
                }
                sb.append(getTextFromPartList(lineTag.getParts()));
            }
        }

        return sb.toString();
    }

    public static String removeAccidentals(String text) {
        String[] replaceFrom = {
                "á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű",
                "Á", "É", "Í", "Ó", "Ö", "Ő", "Ú", "Ü", "Ű"
        };

        String[] replaceTo = {
                "a", "e", "i", "o", "o", "o", "u", "u", "u",
                "A", "E", "I", "O", "O", "O", "U", "U", "U"
        };

        String result = text;
        for (int i = 0; i < replaceFrom.length; i++) {
            result = result.replaceAll(replaceFrom[i], replaceTo[i]);
        }
        return result;
    }

    public static String removePunctuations(String text) {
        String[] replaceFrom = {
                ",", "\\.", "\\?", "!", ":", ";", "„",
                "”", "\\(", "\\)", "–", "…", "’", "\r\n", "\n"
        };

        String[] replaceTo = {
                "", "", "", "", "", "", "",
                "", "", "", "", "", "", " ", " "
        };

        String result = text;
        for (int i = 0; i < replaceFrom.length; i++) {
            result = result.replaceAll(replaceFrom[i], replaceTo[i]);
        }
        return result;
    }

    public static void copyCollectionAndTrackNoToSongbooks(Song song) {
        if (song.getProperties().getCollection() != null) {
            Songbook book = new Songbook().setName(song.getProperties().getCollection());
            if (song.getProperties().getTrackNo() != null) {
                book.setEntry(song.getProperties().getTrackNo().toString());
            }
            song.getProperties().getSongbooks().add(0, book);
        }
    }

    public static void copyFirstSongbookToCollection(Song song) {
        if (!song.getProperties().getSongbooks().isEmpty()) {
            Songbook book = song.getProperties().getSongbooks().get(0);
            song.getProperties().setCollection(book.getName());
            if (book.getEntry() != null) {
                song.getProperties().setTrackNo(parseInt(book.getEntry()));
            }
        }
    }

    public static void extractWordsToSet(Song song, Set<String> wordList) {
        wordList.addAll(
            Arrays.stream(
                removePunctuations(
                    getSongTextContent(song, false)
                )
            .split(" "))
            .filter(w -> !w.isEmpty())
            .collect(Collectors.toSet())
        );
    }

    public static byte[] readAllBytes(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public static String repeat(String what, int occurence) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < occurence; i++) {
            sb.append(what);
        }
        return sb.toString();
    }
}
