package org.openlyrics.jlyrics.transform;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.lyrics.ILyricsEntry;
import org.openlyrics.jlyrics.song.lyrics.MixedContainer;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.lyrics.VerseLine;
import org.openlyrics.jlyrics.song.lyrics.linepart.ILinePart;
import org.openlyrics.jlyrics.song.lyrics.linepart.LineTag;
import org.openlyrics.jlyrics.song.lyrics.linepart.Text;
import org.openlyrics.jlyrics.song.properties.Songbook;
import org.openlyrics.jlyrics.util.VerseUtils;

import java.util.*;

import static java.lang.Integer.parseInt;

public class SongTransformer {

    public Song transform(Song originalSong, SongTransformerConfig config) {

        Song song = originalSong.getDeepCopy();

        addConfiguredVerses(song, config);

        if (config.isIntroSlide()) {
            addIntroSlideToSong(song, config);
        }

        if (config.isEmptySlide()) {
            addEmptySlideToSong(song);
        }

        if (config.isTagSlide()) {
            addTagSlideToSong(song, config);
        }

        return song;
    }

    private void addConfiguredVerses(Song song, SongTransformerConfig config) {
        Map<String, ILyricsEntry> verseMap = new LinkedHashMap<>();
        song.getLyrics().forEach(v -> verseMap.put(v.getName(), v));
        song.getLyrics().clear();

        //if verseOrder is set, then add verses according to it
        if (song.getProperties().getVerseOrder() != null) {
            Set<String> alreadyAdded = new HashSet<>();

            String[] order = song.getProperties().getVerseOrder().split(" ");
            for (String s : order) {
                if (!config.isRepeatVerses() && alreadyAdded.contains(s)) {
                    continue;
                }
                song.getLyrics().add(getConfiguredVerse(verseMap.get(s), config));
                alreadyAdded.add(s);
            }
        }
        else {
            //if no verseOrder, then pick up every verse configured
            for (Map.Entry<String, ILyricsEntry> entry : verseMap.entrySet()) {
                song.getLyrics().add(getConfiguredVerse(entry.getValue(), config));
            }
        }
    }

    private ILyricsEntry getConfiguredVerse(ILyricsEntry entry, SongTransformerConfig config) {
        if (entry instanceof Verse) {
            Verse verse = (Verse) entry;
            verse.getLines().forEach(line -> configureLine(line, config));
        }
        return entry;
    }

    private void configureLine(MixedContainer item, SongTransformerConfig config) {
        List<ILinePart> parts = new ArrayList<>(item.getParts());
        item.getParts().clear();
        boolean afterNewLine = true;
        for (ILinePart part : parts) {
            if (part instanceof Text) {
                Text text = (Text) part;
                if (config.isFirstUppercase() && afterNewLine) {
                    text.setContent(text.getContent().substring(0,1).toUpperCase() + text.getContent().substring(1));
                }
                item.getParts().add(part);
            }
            else {
                LineTag tag = (LineTag) part;
                if (tag.getName().equals("br")) {
                    afterNewLine = true;
                    if (!config.isLineBreak()) {
                        item.getParts().add(new Text().setContent(config.isSolidusSeparator() ? " / " : " "));
                    } else {
                        item.getParts().add(part);
                        configureLine(tag, config);
                    }
                }
                else {
                    item.getParts().add(part);
                    afterNewLine = false;
                    configureLine(tag, config);
                }
            }
        }
    }

    private void addIntroSlideToSong(Song song, SongTransformerConfig config) {
        Verse verse = new Verse();
        int verseNumber = VerseUtils.getHighestVerseNumberForType(song, 'i');
        if (verseNumber > 0) {
            VerseUtils.incrementVerseNumberForType(song, 'i', 1);
        }
        verse.setName("i1");
        VerseLine line = verse.getLines().get(0);

        String title = song.getProperties().getTitles().get(0).getTitle();
        title += (title.endsWith("!") || title.endsWith("?")) ? "" : "...";
        line.getParts().add(new Text().setContent(title));

        if (config.isIntroSongBook() && !song.getProperties().getSongbooks().isEmpty()) {
            Songbook songbook = song.getProperties().getSongbooks().get(0);
            if (!songbook.getName().isEmpty()) {
                line.getParts().add(new LineTag().setName("br"));
                line.getParts().add(new LineTag().setName("br"));
                line.getParts().add(new Text().setContent(songbook.getName()));
            }

            if (config.isIntroSongNumber() && !songbook.getEntry().isEmpty()) {
                line.getParts().add(new LineTag().setName("br"));
                line.getParts().add(new Text().setContent(song.getProperties().getSongbooks().get(0).getEntry() + "."));
            }
        }
        song.getLyrics().add(0, verse);
        if (song.getProperties().getVerseOrder() != null) {
            song.getProperties().setVerseOrder("i1 " + song.getProperties().getVerseOrder());
        }
    }

    private void addEmptySlideToSong(Song song) {
        Verse verse = new Verse();
        int verseNumber = VerseUtils.getHighestVerseNumberForType(song, 'o') + 1;
        verse.setName("o" + verseNumber);
        song.getLyrics().add(verse);
        song.getProperties().setVerseOrder(song.getProperties().getVerseOrder() + " " + verse.getName());
    }

    private void addTagSlideToSong(Song song, SongTransformerConfig config) {
        Verse verse = new Verse();
        int verseNumber = VerseUtils.getHighestVerseNumberForType(song, 'o') + 1;
        verse.setName("o" + verseNumber);

        VerseLine line = verse.getLines().get(0);
        Songbook songbook = song.getProperties().getSongbooks().get(0);
        line.getParts().add(new Text().setContent(
                config.getShortNameForSongbook(songbook.getName()) +
                String.format("%03d", parseInt(songbook.getEntry()))
        ));
        song.getLyrics().add(verse);
    }
}
