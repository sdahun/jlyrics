package org.openlyrics.jlyrics.song.properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeSignatureTest {

    @Test
    void testNewSignatureIsValid() {
        TimeSignature signature = new TimeSignature();
        assertTrue(signature.isValid());
    }

    @Test
    void testParseInvalidString() {
        TimeSignature signature = new TimeSignature();
        assertFalse(signature.parseString("0/63"));
        assertFalse(signature.parseString("1/63"));
        assertFalse(signature.parseString("0/64"));
        assertTrue(signature.isValid());
        assertEquals(4, signature.getNominator());
        assertEquals(4, signature.getDenominator());
    }

    @Test
    void testParseValidString() {
        TimeSignature signature = new TimeSignature();
        assertTrue(signature.parseString("2/16"));
        assertEquals(2, signature.getNominator());
        assertEquals(16, signature.getDenominator());
        assertTrue(signature.isValid());
    }

    @Test
    void testNewSignatureToString() {
        TimeSignature signature = new TimeSignature();
        assertEquals("4/4", signature.toString());
    }

    @Test
    void testParsedSignatureToString() {
        TimeSignature signature = new TimeSignature();
        String expected = "2/16";
        assertTrue(signature.parseString(expected));
        assertEquals(expected, signature.toString());
        assertTrue(signature.isValid());
    }

    @Test
    void testInvalidNominatorTooLow() {
        TimeSignature signature = new TimeSignature().setNominator(0);
        assertEquals("4/4", signature.toString());
    }

    @Test
    void testInvalidNominatorTooHigh() {
        TimeSignature signature = new TimeSignature().setNominator(64);
        assertEquals("4/4", signature.toString());
    }

    @Test
    void testValidNominator() {
        TimeSignature signature = new TimeSignature().setNominator(63);
        assertEquals("63/4", signature.toString());
    }

    @Test
    void testInvalidDenominator() {
        TimeSignature signature = new TimeSignature().setDenominator(5);
        assertEquals("4/4", signature.toString());
    }

    @Test
    void testValidDenominator() {
        TimeSignature signature = new TimeSignature().setDenominator(16);
        assertEquals("4/16", signature.toString());
    }
}