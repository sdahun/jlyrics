package org.openlyrics.jlyrics.writer;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.util.SongUtils;

import java.io.OutputStream;

import static java.lang.Integer.parseInt;
import static org.openlyrics.jlyrics.song.SongConstants.OTHER_COLLECTION;

public interface ILyricsWriter {
    void write(Song song, OutputStream outputStream) throws Exception;

    default String getFullFilename(Song song) {
        return getFilePath(song) + "/" + getFileName(song) + getFileExtension();
    }

    default String getFilePath(Song song) {
        if (!song.getProperties().getSongbooks().isEmpty()) {
            return SongUtils.removeAccidentals(song.getProperties().getSongbooks().get(0).getName());
        } else {
            return OTHER_COLLECTION;
        }
    }

    default String getFileName(Song song) {
        int songNumber = 0;

        if (!song.getProperties().getSongbooks().isEmpty()) {
            songNumber = parseInt(song.getProperties().getSongbooks().get(0).getEntry());
        }

        return String.format("%03d", songNumber) + ". " +
            SongUtils.removeAccidentals(song.getProperties().getTitles().get(0).getTitle());
    }

    String getFileExtension();
}
