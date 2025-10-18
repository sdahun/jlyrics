package org.openlyrics.jlyrics;

import org.junit.jupiter.api.Test;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.lyrics.VerseLine;
import org.openlyrics.jlyrics.song.lyrics.linepart.Text;
import org.openlyrics.jlyrics.util.VerseUtils;

import java.util.ArrayList;

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
        song.getLyrics().add(
            new Verse().setName("v2").setLines(new ArrayList<>() {{
                add((VerseLine) new VerseLine().setParts(new ArrayList<>() {{
                    add(new Text().setContent("Amazing grace! How sweet the sound!"));
                }}));
            }})
        );
        VerseUtils.incrementVerseNumberForType(song, 'v', 2);

        assertEquals("v1", song.getLyrics().get(0).getName());
        assertEquals("v3", song.getLyrics().get(1).getName());
        assertEquals("v1 v3", song.getProperties().getVerseOrder());
    }

}
