package org.openlyrics.jlyrics.song;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SongVersion {
    //These versions have schema files in resources folder to validate against
    V0_6("0.6"),
    V0_7("0.7"),
    V0_8("0.8"),
    V0_9("0.9");

    private final String value;
    SongVersion(String value) {
        this.value = value;
    }

    public static SongVersion getVersionByValue(String value) {
        return Arrays.stream(values()).filter(v -> v.getValue().equals(value)).findFirst().orElse(null);
    }
}
