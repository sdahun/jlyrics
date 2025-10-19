package org.openlyrics.jlyrics.util;

import org.junit.jupiter.api.Test;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.reader.ReaderType;
import org.openlyrics.jlyrics.song.properties.Songbook;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.openlyrics.jlyrics.util.SongUtils.extractWordsToSet;

class SongUtilsTest {

    @Test
    void testDateToString() {
        ZonedDateTime zdt = ZonedDateTime.of(2023, 12, 8, 12, 43, 56, 0, ZoneId.of("+01:00"));
        assertEquals("2023-12-08T12:43:56+01:00", SongUtils.dateToString(zdt));
    }

    @Test
    void testStringToDate() {
        ZonedDateTime zdt = ZonedDateTime.of(2023, 12, 8, 12, 43, 56, 0, ZoneId.of("+01:00"));
        assertTrue(zdt.isEqual(SongUtils.stringToDate("2023-12-08T12:43:56+01:00")));
    }

    @Test
    void testStringToDateWithoutTimezone() {
        ZonedDateTime zdt = ZonedDateTime.of(2023, 12, 8, 12, 43, 56, 0, ZoneId.systemDefault());
        assertTrue(zdt.isEqual(SongUtils.stringToDate("2023-12-08T12:43:56")));
    }

    @Test
    void testStringToDateWithUTC() {
        ZonedDateTime zdt = ZonedDateTime.of(2023, 12, 8, 12, 43, 56, 0, ZoneId.of("UTC"));
        assertTrue(zdt.isEqual(SongUtils.stringToDate("2023-12-08T12:43:56Z")));
    }

    @Test
    void testStringToDateWithInvalidString() {
        assertNull(SongUtils.stringToDate("abracadabra"));
    }

    @Test
    void testRemoveAccidentals() {
        assertEquals("arvizturo tukorfurogep", SongUtils.removeAccidentals("árvíztűrő tükörfúrógép"));
        assertEquals("ARVIZTURO TUKORFUROGEP", SongUtils.removeAccidentals("ÁRVÍZTŰRŐ TÜKÖRFÚRÓGÉP"));
    }

    @Test
    void testCopyCollectionToSongbook() {
        String bookName = "Hymns And Praises";
        Integer trackNo = 123;
        Song song = new Song();
        song.getProperties().setCollection(bookName);
        song.getProperties().setTrackNo(trackNo);
        Songbook expected = new Songbook().setName(bookName).setEntry(trackNo.toString());
        SongUtils.copyCollectionAndTrackNoToSongbooks(song);
        assertEquals(expected, song.getProperties().getSongbooks().get(0));
    }

    @Test
    void testCopySongbookToCollection() {
        String bookName = "Hymns And Praises";
        Integer trackNo = 123;
        Song song = new Song();
        song.getProperties().getSongbooks().add(new Songbook().setName(bookName).setEntry(trackNo.toString()));
        SongUtils.copyFirstSongbookToCollection(song);
        assertEquals(bookName, song.getProperties().getCollection());
        assertEquals(trackNo, song.getProperties().getTrackNo());
    }

    @Test
    void testExtractWordsToSet() throws Exception {
        Set<String> expected = new HashSet<>(Arrays.asList(
                "'Tis", "Amazing", "God's", "How", "I", "I'm", "Through", "Twas", "We've", "When", "a", "already",
                "and", "appear", "as", "been", "begun", "believed", "blind", "bright", "brought", "but", "come",
                "dangers", "days", "did", "far", "fear", "fears", "first", "found", "grace", "have", "heart", "home",
                "hour", "how", "lead", "less", "like", "lost", "many", "me", "my", "no", "now", "once", "praise",
                "precious", "relieved", "safe", "saved", "see", "shining", "sing", "snares", "sound", "sun", "sweet",
                "taught", "ten", "than", "that", "the", "there", "thousand", "thus", "to", "toils", "was", "we'd",
                "we've", "when", "will", "wretch", "years"));

        Set<String> wordList = new TreeSet<>();
        try (
            InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("songs/original/Amazing Grace.xml")
        ) {
            Song song = IOFactory.getNewReader(ReaderType.OPENLYRICS).read(xmlStream);
            extractWordsToSet(song, wordList);
            assertEquals(expected, wordList);
        }
    }
}