package org.openlyrics.jlyrics;

import org.junit.jupiter.api.Test;
import org.openlyrics.jlyrics.reader.ILyricsReader;
import org.openlyrics.jlyrics.reader.OpenLyricsReader;
import org.openlyrics.jlyrics.reader.ReaderType;
import org.openlyrics.jlyrics.writer.ILyricsWriter;
import org.openlyrics.jlyrics.writer.WriterType;
import org.openlyrics.jlyrics.writer.OpenLyricsWriter;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class IOFactoryTest {
    @Test
    void testCreateOpenLyricsReader() throws Exception {
        ILyricsReader reader = IOFactory.getNewReader(ReaderType.OPENLYRICS);
        assertInstanceOf(OpenLyricsReader.class, reader);
    }

    @Test
    void testCreateOpenLyricsWriter() throws Exception {
        ILyricsWriter writer = IOFactory.getNewWriter(WriterType.OPENLYRICS);
        assertInstanceOf(OpenLyricsWriter.class, writer);
    }
}
