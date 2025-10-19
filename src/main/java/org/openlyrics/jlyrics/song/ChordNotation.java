package org.openlyrics.jlyrics.song;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ChordNotation {
    ENGLISH("english"),
    ENGLISH_B("english-b"),
    GERMAN("german"),
    DUTCH("dutch"),
    HUNGARIAN("hungarian"),
    NEOLATIN("neolatin");

    private final String name;

    ChordNotation(String name) {
        this.name = name;
    }

    public static ChordNotation getChordNotationByName(String name) {
        return Arrays.stream(values()).filter(n -> n.getName().equals(name)).findFirst().orElse(null);
    }
}
