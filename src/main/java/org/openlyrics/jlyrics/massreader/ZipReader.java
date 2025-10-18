package org.openlyrics.jlyrics.massreader;

import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.exception.LyricsException;
import org.openlyrics.jlyrics.reader.ILyricsReader;
import org.openlyrics.jlyrics.reader.ReaderType;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipReader implements IMassReader {
    ILyricsReader reader;
    ZipFile zipFile;
    Enumeration<? extends ZipEntry> entries;

    @Override
    public IMassReader init(String path) throws Exception {
        return this.init(path, IOFactory.getNewReader(ReaderType.OPENLYRICS));
    }

    @Override
    public IMassReader init(String path, ILyricsReader reader) throws Exception {
        this.reader = reader;
        this.zipFile = new ZipFile(path);
        this.entries = zipFile.entries();
        return this;
    }

    @Override
    public void close() throws Exception {
        this.zipFile.close();
    }

    @Override
    public boolean hasNext() {
        return entries.hasMoreElements();
    }

    @Override
    public Song next() {
        ZipEntry entry = entries.nextElement();
        try {
            return this.reader.read(this.zipFile.getInputStream(entry));
        } catch (Exception e) {
            //Iterator.next() implementation can't throw checked exceptions
            throw new LyricsException(e.getMessage());
        }
    }
}
