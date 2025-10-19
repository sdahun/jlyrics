package org.openlyrics.jlyrics.masswriter;

import lombok.Getter;

@Getter
public enum MassWriterType {
    OPENLP(OpenLPWriter.class),
    ZIP(ZipWriter.class),
    EASYWORSHIP(EasyWorshipWriter.class);

    private final Class<? extends IMassWriter> writerClass;

    MassWriterType(Class<? extends IMassWriter> writerClass) {
        this.writerClass = writerClass;
    }
}
