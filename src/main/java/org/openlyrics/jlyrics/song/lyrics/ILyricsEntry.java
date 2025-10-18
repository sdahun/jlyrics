package org.openlyrics.jlyrics.song.lyrics;

public interface ILyricsEntry {
    String getName();
    ILyricsEntry setName(String name);
    ILyricsEntry getDeepCopy();
}
