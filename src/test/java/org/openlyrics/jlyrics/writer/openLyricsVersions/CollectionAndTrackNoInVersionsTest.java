package org.openlyrics.jlyrics.writer.openLyricsVersions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.SongVersion;
import org.openlyrics.jlyrics.writer.WriterType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionAndTrackNoInVersionsTest {
    private Song song;

    @BeforeEach
    void setupSong() {
        this.song = new Song();
        this.song.setModifiedDate("2023-12-31T12:00:00+01:00");
        this.song.getProperties().setCollection("Hymns And Praises");
        this.song.getProperties().setTrackNo(123);
    }

    @ParameterizedTest
    @EnumSource(SongVersion.class)
    void testFormatInVersion(SongVersion songVersion) throws Exception {
        this.song.setVersion(songVersion);
        try (
                InputStream expectedXml = this.getClass().getClassLoader().getResourceAsStream("expected/collectionAndTrackNoIn" + songVersion.toString() + ".xml");
                ByteArrayOutputStream generatedXml = new ByteArrayOutputStream()
        ) {
            String expected = (expectedXml != null) ? new String(expectedXml.readAllBytes()) : "";
            IOFactory.getNewWriter(WriterType.OPENLYRICS).write(song, generatedXml);
            assertEquals(expected, generatedXml.toString());
        }
    }
}
