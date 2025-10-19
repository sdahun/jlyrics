package org.openlyrics.jlyrics.song;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SongAttribute {
    VERSION("version"),
    XML_LANG("xml:lang"),
    CREATED_IN("createdIn"),
    MODIFIED_IN("modifiedIn"),
    MODIFIED_DATE("modifiedDate"),
    CHORD_NOTATION("chordNotation");

    private final String name;
    SongAttribute(String name) {
        this.name = name;
    }

    public static SongAttribute getAttributeByName(String name) {
        return Arrays.stream(values()).filter(a -> a.getName().equals(name)).findFirst().orElse(null);
    }
}
