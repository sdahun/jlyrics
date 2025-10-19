package org.openlyrics.jlyrics.validation;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.lyrics.ILyricsEntry;
import org.openlyrics.jlyrics.song.lyrics.Verse;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class VerseOrderValidator implements IValidator {
    @Override
    public boolean isValid(Song song) {

        Set<String> versesFromOrder = Arrays.stream(song.getProperties().getVerseOrder().split(" "))
                .collect(Collectors.toSet());

        Set<String> versesFromNames = song.getLyrics().stream()
                .filter(v -> v instanceof Verse)
                .map(ILyricsEntry::getName)
                .collect(Collectors.toSet());

        return versesFromOrder.equals(versesFromNames);
    }
}
