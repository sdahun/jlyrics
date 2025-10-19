package org.openlyrics.jlyrics.song.lyrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InstrumentLineTest {

    @Test
    void testInstrumentLineRepeatLowerThanTwo() {
        InstrumentLine line = new InstrumentLine().setRepeat(1);
        assertNull(line.getRepeat());
    }

    @Test
    void testInstrumentLineRepeatGreaterThanOne() {
        InstrumentLine line = new InstrumentLine().setRepeat(2);
        assertEquals(2, line.getRepeat());
    }

}