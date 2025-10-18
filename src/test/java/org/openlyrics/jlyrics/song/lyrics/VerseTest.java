package org.openlyrics.jlyrics.song.lyrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VerseTest {

    @Test
    void testVerseAsILyricsEntryImplementation() {
        ILyricsEntry verse = new Verse().setName("v1");
        assertEquals("v1", verse.getName());
    }

}