package org.openlyrics.jlyrics.transform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class SongTransformerConfig {
    private boolean introSlide = true;
    private boolean introSongBook = false;
    private boolean introSongNumber = false;

    private boolean lineBreak = true;
    private boolean solidusSeparator = false;
    private boolean firstUppercase = false;
    private boolean repeatVerses = false;

    private boolean emptySlide = false;
    private boolean tagSlide = false;

    @JsonIgnore
    private List<ConfigSongBookData> songbooks = new ArrayList<>();

    private int writerFormat;
    private int batchSize;
    private Map<Integer, String> selectedSongs = new LinkedHashMap<>();

    public SongTransformerConfig addSongbookData(ConfigSongBookData data) {
        songbooks.add(data);
        return this;
    }

    public ConfigSongBookData getSongbookById(int index) {
        return songbooks.get(index);
    }

    @JsonIgnore
    public List<String> getSongbookNames() {
        return songbooks.stream()
            .map(ConfigSongBookData::getName)
            .collect(Collectors.toList());
    }

    public String getShortNameForSongbook(String songbook) {
        return songbooks.stream()
            .filter(book -> book.getName().equals(songbook))
            .map(ConfigSongBookData::getAbbreviation)
            .findFirst()
            .orElse("@");
    }
}
