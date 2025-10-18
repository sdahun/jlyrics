package org.openlyrics.jlyrics.massreader;

import lombok.Getter;

@Getter
public enum MassReaderType {
    OPENLP_DB(OpenLPDBReader.class),
    OPENLP_SERVICE(OpenLPServiceReader.class),
    ZIP(ZipReader.class);

    private final Class<? extends IMassReader> readerClass;

    MassReaderType(Class<? extends IMassReader> readerClass) {
        this.readerClass = readerClass;
    }
}
