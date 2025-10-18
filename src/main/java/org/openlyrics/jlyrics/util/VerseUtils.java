package org.openlyrics.jlyrics.util;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.lyrics.ILyricsEntry;
import org.openlyrics.jlyrics.song.lyrics.Verse;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class VerseUtils {

    public static final String VERSE_NAME_EXTRACT_REGEX = "^[vcpboie](\\d+)(.*)$";

    public static String getVerseNameByChar(char abbreviation) {
        return switch (abbreviation) {
            case 'v' -> "Verse";
            case 'c' -> "Chorus";
            case 'p' -> "Pre-Chorus";
            case 'b' -> "Bridge";
            case 'i' -> "Intro";
            case 'e' -> "Ending";
            default -> "Other";
        };
    }

    public static List<ILyricsEntry> getLyricsEntriesByName(Song song, String name) {
        return song.getLyrics().stream().filter(entry -> entry.getName().equals(name)).collect(Collectors.toList());
    }

    public static List<String> getAllVerseNames(Song song) {
        return song.getLyrics().stream()
                .filter(v -> v instanceof Verse)
                .map(ILyricsEntry::getName)
                .collect(Collectors.toList());
    }

    public static int getHighestVerseNumberForType(Song song, char verseType) {
        Pattern pattern = Pattern.compile(VERSE_NAME_EXTRACT_REGEX);

        return getAllVerseNames(song).stream()
                .filter(name -> name.charAt(0) == verseType)
                .mapToInt(name -> {
                    Matcher matcher = pattern.matcher(name);
                    return matcher.find() ? parseInt(matcher.group(1)) : 0;
                })
                .max()
                .orElse(0);
    }

    public static void incrementVerseNumberForType(Song song, char verseType, int from) {
        Pattern pattern = Pattern.compile(VERSE_NAME_EXTRACT_REGEX);

        for (ILyricsEntry entry : song.getLyrics()) {
            if (entry instanceof Verse && entry.getName().charAt(0) == verseType) {
                Matcher matcher = pattern.matcher(entry.getName());
                if (matcher.find()) {
                    int currentNumber = parseInt(matcher.group(1));
                    if (currentNumber >= from) {
                        ++currentNumber;
                        entry.setName(String.valueOf(verseType) + currentNumber + matcher.group(2));
                    }
                }
            }
        }

        String[] order = song.getProperties().getVerseOrder().split(" ");
        for (int i = 0; i < order.length; i++) {
            if (order[i].charAt(0) == verseType) {
                Matcher matcher = pattern.matcher(order[i]);
                if (matcher.find()) {
                    int currentNumber = parseInt(matcher.group(1));
                    if (currentNumber >= from) {
                        ++currentNumber;
                        order[i] = String.valueOf(verseType) + currentNumber + matcher.group(2);
                    }
                }
            }
        }
        song.getProperties().setVerseOrder(String.join(" ", order));
    }

}
