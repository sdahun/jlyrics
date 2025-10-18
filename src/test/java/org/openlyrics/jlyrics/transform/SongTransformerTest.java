package org.openlyrics.jlyrics.transform;

import org.junit.jupiter.api.Test;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.writer.WriterType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SongTransformerTest {
    @Test
    void testTransform() throws Exception {
        Song song = new Song();
        song.setModifiedDate("2023-12-31T12:00:00+01:00");

        SongTransformerConfig config = new SongTransformerConfig()
            .setIntroSlide(true)
            .setIntroSongBook(true)
            .setIntroSongNumber(true)

            .setLineBreak(true)
            .setFirstUppercase(true)
            .setRepeatVerses(false)

            .setEmptySlide(false)
            .setTagSlide(false)

            .addSongbookData(new ConfigSongBookData("HE", "Hitünk énekei", "hitunk_enekei"));

        song = new SongTransformer().transform(song, config);

        try (
            InputStream expectedStream = this.getClass().getClassLoader().getResourceAsStream("transform/transformed_empty.xml");
            ByteArrayOutputStream generatedXml = new ByteArrayOutputStream()
        ) {
            String expected = (expectedStream != null) ? new String(expectedStream.readAllBytes()) : "";
            IOFactory.getNewWriter(WriterType.OPENLYRICS).write(song, generatedXml);
            assertEquals(expected, generatedXml.toString());
        }
    }

    @Test
    void regexTest() {
        Pattern pattern = Pattern.compile("^[vcpboie](\\d+).*$");
        Matcher matcher = pattern.matcher("o1");
        assertTrue(matcher.find());
        assertEquals("1", matcher.group(1));
    }
}
