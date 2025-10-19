package org.openlyrics.jlyrics.reader;

import org.openlyrics.jlyrics.Song;

import java.io.InputStream;

public interface ILyricsReader {
    Song read(InputStream inputStream) throws Exception;
}
