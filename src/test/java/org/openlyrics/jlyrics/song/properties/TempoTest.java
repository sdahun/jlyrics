package org.openlyrics.jlyrics.song.properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TempoTest {

    @Test
    void testSetNumberValueWhenTypeIsNull() {
        Tempo tempo = new Tempo().setValue("80");

        assertNull(tempo.getType());
        assertNull(tempo.getValue());
    }

    @Test
    void testSetStringValueWhenTypeIsNull() {
        Tempo tempo = new Tempo().setValue("Moderato");

        assertNull(tempo.getType());
        assertNull(tempo.getValue());
    }

    @Test
    void testNewTempoIsEmpty() {
        Tempo tempo = new Tempo();
        assertTrue(tempo.isEmpty());
    }

    @Test
    void testClearMakesTempoEmpty() {
        Tempo tempo = new Tempo().setType(TempoType.BPM).setValue("80");
        tempo.clear();

        assertTrue(tempo.isEmpty());
        assertNull(tempo.getType());
        assertNull(tempo.getValue());
    }

    @Test
    void testTempoInBPMButValueIsString() {
        Tempo tempo = new Tempo().setType(TempoType.BPM).setValue("Moderato");

        assertEquals(TempoType.BPM, tempo.getType());
        assertNull(tempo.getValue());
    }

    @Test
    void testTempoInBPM() {
        Tempo tempo = new Tempo().setType(TempoType.BPM).setValue("80");

        assertEquals(TempoType.BPM, tempo.getType());
        assertEquals("80", tempo.getValue());
    }

    @Test
    void testTempoInText() {
        Tempo tempo = new Tempo().setType(TempoType.TEXT).setValue("Moderato");

        assertEquals(TempoType.TEXT, tempo.getType());
        assertEquals("Moderato", tempo.getValue());
    }

    @Test
    void testTempoIsTooSlow() {
        Tempo tempo = new Tempo().setType(TempoType.BPM).setValue("29");

        assertEquals(TempoType.BPM, tempo.getType());
        assertNull(tempo.getValue());
    }

    @Test
    void testTempoIsTooFast() {
        Tempo tempo = new Tempo().setType(TempoType.BPM).setValue("251");

        assertEquals(TempoType.BPM, tempo.getType());
        assertNull(tempo.getValue());
    }
}
