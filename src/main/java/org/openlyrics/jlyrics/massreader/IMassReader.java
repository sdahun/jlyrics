package org.openlyrics.jlyrics.massreader;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.reader.ILyricsReader;

import java.util.Iterator;

public interface IMassReader extends Iterator<Song>, AutoCloseable {

    // Exception can be IOException or SQLException also, depending on implementation
    IMassReader init(String path) throws Exception;
    IMassReader init(String path, ILyricsReader reader) throws Exception;
}
