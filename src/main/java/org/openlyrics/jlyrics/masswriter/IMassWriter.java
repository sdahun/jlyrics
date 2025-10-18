package org.openlyrics.jlyrics.masswriter;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.transform.SongTransformerConfig;
import org.openlyrics.jlyrics.writer.ILyricsWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface IMassWriter extends AutoCloseable {

    // Exception can be IOException or SQLException also, depending on implementation
    IMassWriter init(String path, ILyricsWriter writer, SongTransformerConfig config) throws Exception;
    IMassWriter add(Song song) throws Exception;
    String getFileExtension();

    default String generateFilename() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm.ss");
        return "sdahun_songs_" + dtf.format(LocalDateTime.now());
    }
}
