package org.openlyrics.jlyrics.song.lyrics.linepart;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MusicalNote {
    C("C"),
    Cis("C#"),
    Des("Db"),
    D("D"),
    Dis("D#"),
    Ees("Eb"),
    E("E"),
    F("F"),
    Fis("F#"),
    Ges("Gb"),
    G("G"),
    Gis("G#"),
    Aes("Ab"),
    A("A"),
    Ais("A#"),
    Bes("Bb"),
    B("B"),
    Eis("E#"),
    Bis("B#"),
    Fisis("Fx"),
    Cisis("Cx"),
    Gisis("Gx"),
    Ces("Cb"),
    Fes("Fb");

    private final String name;

    MusicalNote(String name) {
        this.name = name;
    }

    public static MusicalNote getNoteByName(String name) {
        return Arrays.stream(values()).filter(v -> v.getName().equals(name)).findFirst().orElse(null);
    }
}
