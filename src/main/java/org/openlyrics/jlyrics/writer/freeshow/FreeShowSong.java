package org.openlyrics.jlyrics.writer.freeshow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.lyrics.linepart.LineTag;
import org.openlyrics.jlyrics.song.lyrics.linepart.Text;
import org.openlyrics.jlyrics.song.properties.Author;
import org.openlyrics.jlyrics.song.properties.AuthorType;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Data
@JsonPropertyOrder({"name", "private", "category", "settings", "timestamps", "quickAccess"})
public class FreeShowSong {

    @JsonIgnore
    private String uid;

    private String name;

    @JsonProperty("private")
    private Boolean isPrivate = false;

    private String category = "song";

    private Map<String, String> settings = new LinkedHashMap<>();

    private Map<String, Long> timestamps = new LinkedHashMap<>();

    private Map<String, Object> quickAccess = new LinkedHashMap<>();

    private Map<String, String> meta = new LinkedHashMap<>();

    private Map<String, FreeShowSlide> slides = new LinkedHashMap<>();

    private Map<String, FreeShowLayout> layouts = new LinkedHashMap<>();

    private Map<String, String> media = new LinkedHashMap<>();

    @JsonProperty("locked")
    private Boolean isLocked = true;

    public FreeShowSong(Song song) {
        this.uid = getNewUid();

        this.timestamps.put("created", Instant.now().getEpochSecond() * 1000);
        this.timestamps.put("modified", null);
        this.timestamps.put("used", null);

        //song number
        if (!song.getProperties().getSongbooks().isEmpty()) {
            String songNumber = song.getProperties().getSongbooks().get(0).getEntry();
            this.quickAccess.put("number", songNumber);
            this.meta.put("number", songNumber);
        }

        //title
        String title = song.getProperties().getTitles().get(0).getTitle();
        this.name = title;
        if (!title.isEmpty()) {
            this.meta.put("title", title);
        }

        //author, composer
        if (!song.getProperties().getAuthors().isEmpty()) {
            this.meta.put("author", song.getProperties().getAuthors().stream()
                    .filter(a -> a.getType() == AuthorType.WORDS)
                    .map(Author::getName)
                    .findFirst()
                    .orElse(song.getProperties().getAuthors().get(0).getName())
            );
            this.meta.put("composer", song.getProperties().getAuthors().stream()
                    .filter(a -> a.getType() == AuthorType.MUSIC)
                    .map(Author::getName)
                    .findFirst()
                    .orElse(song.getProperties().getAuthors().get(0).getName()));
        }

        //copyright
        if (song.getProperties().getCopyright() != null) {
            this.meta.put("copyright", song.getProperties().getCopyright());
        }

        //CCLI
        if (song.getProperties().getCcliNo() != null) {
            this.meta.put("CCLI", Integer.toString(song.getProperties().getCcliNo()));
            this.quickAccess.put("metadata", new HashMap<String, String>() {{
                put("CCLI", Integer.toString(song.getProperties().getCcliNo()));
            }});
        }

        //key
        if (song.getProperties().getKey() != null) {
            this.meta.put("key", song.getProperties().getKey());
        }

        Map<String, String> verseIds = new HashMap<>();
        song.getLyrics().forEach(lyricsEntry -> {
            if (lyricsEntry instanceof Verse verse) {
                FreeShowSlide slide = new FreeShowSlide();
                slide.setUid(getNewUid());
                slide.setByVerseName(verse.getName());
                verseIds.put(verse.getName(), slide.getUid());

                StringBuilder sb = new StringBuilder();
                verse.getLines().forEach(verseLine ->
                    verseLine.getParts().forEach(part -> {
                        if (part instanceof Text text) {
                            sb.append(text.getContent());
                        }
                        if (part instanceof LineTag tag && tag.getName().equals("br")) {
                            sb.append("\n");
                        }
                    })
                );

                FreeShowItem textbox = new FreeShowItem();
                for (String lineText : sb.toString().split("\n")) {
                    FreeShowLine line = new FreeShowLine();
                    line.addText(lineText);
                    textbox.addLine(line);
                }
                slide.addItem(textbox);
                slides.put(slide.getUid(), slide);
            }
        });

        String layoutUid = getNewUid();
        FreeShowLayout layout = new FreeShowLayout();
        for (String verseName : song.getProperties().getVerseOrder().split(" ")) {
            layout.addSlide(new FreeShowLayoutItem(verseIds.get(verseName)));
        }

        this.layouts.put(layoutUid, layout);

        this.settings.put("activeLayout", layoutUid);
        this.settings.put("template", "default");
    }

    private String getNewUid() {
        return UUID.randomUUID().toString().substring(24, 35);
    }
}
