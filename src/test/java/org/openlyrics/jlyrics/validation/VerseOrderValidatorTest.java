package org.openlyrics.jlyrics.validation;

import org.junit.jupiter.api.Test;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.lyrics.Verse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VerseOrderValidatorTest {

    @Test
    void testMoreInVerseOrder() {
        Song song = new Song();
        IValidator validator = new VerseOrderValidator();
        song.getProperties().setVerseOrder("v1 v2");
        assertFalse(validator.isValid(song));
    }

    @Test
    void testLessInVerseOrder() {
        Song song = new Song();
        song.getProperties().setVerseOrder("");
        assertFalse(new VerseOrderValidator().isValid(song));
    }

    @Test
    void testEqualsVerseOrder() {
        Song song = new Song();
        song.getLyrics().add(new Verse().setName("v2"));
        song.getProperties().setVerseOrder("v1 v2");
        assertTrue(new VerseOrderValidator().isValid(song));
    }
}
