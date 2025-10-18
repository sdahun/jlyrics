package org.openlyrics.jlyrics.song.lyrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VerseLineTest {

    @Test
    void testOptionalBreakNotOptional() {
        VerseLine line = new VerseLine().setOptionalBreak("unconditional");
        assertNull(line.getOptionalBreak());
    }

    @Test
    void testOptionalBreakIsOptional() {
        VerseLine line = new VerseLine().setOptionalBreak("optional");
        assertEquals("optional", line.getOptionalBreak());
    }

    @Test
    void testRepeatNegative() {
        VerseLine line = new VerseLine().setRepeat(-20);
        assertNull(line.getRepeat());
    }

    @Test
    void testRepeatOne() {
        VerseLine line = new VerseLine().setRepeat(1);
        assertNull(line.getRepeat());
    }

    @Test
    void testRepeatTwo() {
        VerseLine line = new VerseLine().setRepeat(2);
        assertEquals(2, line.getRepeat());
    }
}
