package org.openlyrics.jlyrics.reader;

import lombok.Getter;

@Getter
public enum ReaderType {
    OPENLYRICS(OpenLyricsReader.class);

    private final Class<? extends ILyricsReader> readerClass;
    ReaderType(Class<? extends ILyricsReader> readerClass) {
        this.readerClass = readerClass;
    }
}
