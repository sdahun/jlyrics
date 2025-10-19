package org.openlyrics.jlyrics;

import org.junit.jupiter.api.Test;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.lyrics.VerseLine;
import org.openlyrics.jlyrics.song.lyrics.linepart.ILinePart;
import org.openlyrics.jlyrics.song.lyrics.linepart.Text;
import org.openlyrics.jlyrics.util.VerseUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SongTest {

    @Test
    void testEmptySongHasOneV1Entry() {
        Song song = new Song();
        assertEquals(1, VerseUtils.getLyricsEntriesByName(song, "v1").size());
    }

    @Test
    void testEmptySongHasNotV2Entry() {
        Song song = new Song();
        assertTrue(VerseUtils.getLyricsEntriesByName(song, "v2").isEmpty());
    }

    @Test
    void testIncrementVerseNumberForType() {
        Song song = new Song();
        song.getProperties().setVerseOrder("v1 v2");
        List<ILinePart> lineParts = new ArrayList<>();
        lineParts.add(new Text().setContent("Amazing grace! How sweet the sound!"));

        List<VerseLine> lines = new ArrayList<>();
        lines.add((VerseLine) new VerseLine().setParts(lineParts));

        song.getLyrics().add(new Verse().setName("v2").setLines(lines));

        VerseUtils.incrementVerseNumberForType(song, 'v', 2);

        assertEquals("v1", song.getLyrics().get(0).getName());
        assertEquals("v3", song.getLyrics().get(1).getName());
        assertEquals("v1 v3", song.getProperties().getVerseOrder());
    }

}
