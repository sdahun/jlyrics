package org.openlyrics.jlyrics.writer;

import lombok.Getter;

@Getter
public enum WriterType {
    OPENLYRICS(OpenLyricsWriter.class),
    TEXT(TextWriter.class),
    PPTX(PptxWriter.class),
    RTF(RtfWriter.class),
    FREESHOW(FreeShowWriter.class);

    private final Class<? extends ILyricsWriter> writerClass;

    WriterType(Class<? extends ILyricsWriter> writerClass) {
        this.writerClass = writerClass;
    }
}
