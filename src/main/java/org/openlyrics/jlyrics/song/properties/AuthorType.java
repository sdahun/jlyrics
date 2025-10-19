package org.openlyrics.jlyrics.song.properties;

public enum AuthorType {
    WORDS,
    MUSIC,
    TRANSLATION,
    ARRANGEMENT;

    public String getName() {
        return this.name().toLowerCase();
    }
}
