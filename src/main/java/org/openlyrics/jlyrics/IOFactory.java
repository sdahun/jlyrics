package org.openlyrics.jlyrics;

import org.openlyrics.jlyrics.massreader.IMassReader;
import org.openlyrics.jlyrics.massreader.MassReaderType;
import org.openlyrics.jlyrics.masswriter.IMassWriter;
import org.openlyrics.jlyrics.masswriter.MassWriterType;
import org.openlyrics.jlyrics.reader.ILyricsReader;
import org.openlyrics.jlyrics.reader.ReaderType;
import org.openlyrics.jlyrics.writer.ILyricsWriter;
import org.openlyrics.jlyrics.writer.WriterType;

public class IOFactory {
    private IOFactory() {
    }

    public static ILyricsReader getNewReader(ReaderType readerType) throws Exception {
        return readerType.getReaderClass().getDeclaredConstructor().newInstance();
    }

    public static IMassReader getNewMassReader(MassReaderType massReaderType) throws Exception {
        return massReaderType.getReaderClass().getDeclaredConstructor().newInstance();
    }

    public static ILyricsWriter getNewWriter(WriterType writerType) throws Exception {
        return writerType.getWriterClass().getDeclaredConstructor().newInstance();
    }

    public static IMassWriter getNewMassWriter(MassWriterType massWriterType) throws Exception {
        return massWriterType.getWriterClass().getDeclaredConstructor().newInstance();
    }
}
