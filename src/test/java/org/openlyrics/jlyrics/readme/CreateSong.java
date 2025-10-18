package org.openlyrics.jlyrics.readme;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.Properties;
import org.openlyrics.jlyrics.song.SongVersion;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.lyrics.VerseLine;
import org.openlyrics.jlyrics.song.lyrics.linepart.LineTag;
import org.openlyrics.jlyrics.song.lyrics.linepart.Text;
import org.openlyrics.jlyrics.song.properties.Author;
import org.openlyrics.jlyrics.song.properties.Theme;
import org.openlyrics.jlyrics.song.properties.Title;
import org.openlyrics.jlyrics.util.SongUtils;
import org.openlyrics.jlyrics.writer.OpenLyricsWriter;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class CreateSong {
    public static void main(String[] args) {
        Song song = new Song()
            .setVersion(SongVersion.V0_8)
            .setCreatedIn("SmartLyrics 1.0")
            .setModifiedIn("SmartLyrics 1.0")
            .setModifiedDate(SongUtils.dateToString(ZonedDateTime.now()))
            .setProperties(new Properties()
                .setTitles(new ArrayList<>() {{
                    add(new Title().setTitle("Amazing Grace"));
                    add(new Title()
                        .setTitle("Az Úr irgalma végtelen")
                        .setLang("hu")
                    );
                }})
                .setAuthors(new ArrayList<>() {{
                    add(new Author().setName("John Newton"));
                }})
                .setCopyright("1982 Jubilate Hymns Limited")
                .setCcliNo(1037882)
                .setThemes(new ArrayList<>() {{
                    add(new Theme().setTheme("God's Attributes"));
                }})
            )
            .setLyrics(new ArrayList<>() {{
                add(new Verse().setLines(new ArrayList<>() {{
                add((VerseLine) new VerseLine().setParts(new ArrayList<>() {{
                    add(new Text().setContent("Amazing grace! How sweet the sound!"));
                    add(new LineTag().setName("br"));
                    add(new Text().setContent("That saved a wretch like me!"));
                    add(new LineTag().setName("br"));
                    add(new Text().setContent("I once was lost, but now I'm found,"));
                    add(new LineTag().setName("br"));
                    add(new Text().setContent("Was blind, but now I see."));
                }}));
            }}));
        }});

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            OpenLyricsWriter writer = new OpenLyricsWriter();
            writer.write(song, baos);
            System.out.println(baos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
