package org.openlyrics.jlyrics.transform;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConfigSongBookData {
    private String abbreviation;
    private String name;
    private String folder;

    public ConfigSongBookData(){}

    public ConfigSongBookData(String abbreviation, String name, String folder) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.folder = folder;
    }
}
