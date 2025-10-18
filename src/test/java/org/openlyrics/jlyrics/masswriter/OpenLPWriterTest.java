package org.openlyrics.jlyrics.masswriter;

import org.junit.jupiter.api.Test;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.lyrics.VerseLine;
import org.openlyrics.jlyrics.song.lyrics.linepart.LineTag;
import org.openlyrics.jlyrics.song.lyrics.linepart.Text;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenLPWriterTest {

/*
    @Test
    void testOpenLPWriter() throws Exception {
        IOFactory.getNewMassWriter(MassWriterType.OPENLP).init("masswrited.sql", null).close();

    }

    @Test
    void testLyricsWriter() throws Exception {
        Song song = IOFactory.getNewReader(ReaderType.OPENLYRICS).read(new FileInputStream("001.xml"));
        IOFactory.getNewMassWriter(MassWriterType.OPENLP).init("first.sqlite", null).add(song).close();
    }
*/

    @Test
    void testGetSearchTitle() {
        String songTitle = "131. Ó! Úr Jézus! Egeknek; nagy Királya!";
        Song song = new Song();
        song.getProperties().getTitles().get(0).setTitle(songTitle);
        String expected = "131 ó úr jézus egeknek nagy királya@";

        assertEquals(expected, new OpenLPWriter().getSearchTitle(song));
    }

    @Test
    void testGetSearchLyrics() {
        Song song = new Song();
        VerseLine line = ((Verse) song.getLyrics().get(0)).getLines().get(0);
        line.getParts().add(new Text().setContent("Első; versszak, első sor."));
        line.getParts().add(new LineTag().setName("br"));
        line.getParts().add(new Text().setContent("Első versszak - Második sor!"));

        String expected = "első versszak első sor első versszak második sor";
        assertEquals(expected, new OpenLPWriter().getSearchLyrics(song));
    }

    @Test
    void testNameSplitWithSpace() {
        String fullName = "John Doe";
        List<String> expected = new ArrayList<>() {{ add("John"); add("Doe"); }};
        assertEquals(expected, new OpenLPWriter().splitNameToFirstAndLast(fullName));
    }

    @Test
    void testNameSplitWithoutSpace() {
        String fullName = "PeterPan";
        List<String> expected = new ArrayList<>() {{ add("PeterPan"); add(""); }};
        assertEquals(expected, new OpenLPWriter().splitNameToFirstAndLast(fullName));
    }
}