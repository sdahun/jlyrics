package org.openlyrics.jlyrics.writer;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.util.SongUtils;

import java.io.OutputStream;

public class TextWriter implements ILyricsWriter {

    @Override
    public String getFileExtension() {
        return ".txt";
    }

    @Override
    public void write(Song song, OutputStream outputStream) throws Exception {
        outputStream.write(SongUtils.getSongTextContent(song).getBytes());
    }

}
