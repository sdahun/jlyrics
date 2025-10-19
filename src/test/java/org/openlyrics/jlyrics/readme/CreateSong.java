package org.openlyrics.jlyrics.readme;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.Properties;
import org.openlyrics.jlyrics.song.SongVersion;
import org.openlyrics.jlyrics.song.lyrics.ILyricsEntry;
import org.openlyrics.jlyrics.song.lyrics.MixedContainer;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.lyrics.VerseLine;
import org.openlyrics.jlyrics.song.lyrics.linepart.ILinePart;
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
import java.util.List;

public class CreateSong {
    public static void main(String[] args) {

        List<Title> titles = new ArrayList<>();
        titles.add(new Title().setTitle("Amazing Grace"));
        titles.add(new Title()
                .setTitle("Az Úr irgalma végtelen")
                .setLang("hu")
        );

        List<Author> authors = new ArrayList<>();
        authors.add(new Author().setName("John Newton"));

        List<Theme> themes = new ArrayList<>();
        themes.add(new Theme().setTheme("God's Attributes"));

        List<ILinePart> lineParts = new ArrayList<>();
        lineParts.add(new Text().setContent("Amazing grace! How sweet the sound!"));
        lineParts.add(new LineTag().setName("br"));
        lineParts.add(new Text().setContent("That saved a wretch like me!"));
        lineParts.add(new LineTag().setName("br"));
        lineParts.add(new Text().setContent("I once was lost, but now I'm found,"));
        lineParts.add(new LineTag().setName("br"));
        lineParts.add(new Text().setContent("Was blind, but now I see."));

        List<VerseLine> lines = new ArrayList<>();
        lines.add((VerseLine) new VerseLine().setParts(lineParts));

        List<ILyricsEntry> verses = new ArrayList<>();
        verses.add(new Verse().setName("v1").setLines(lines));

        Song song = new Song()
            .setVersion(SongVersion.V0_8)
            .setCreatedIn("SmartLyrics 1.0")
            .setModifiedIn("SmartLyrics 1.0")
            .setModifiedDate(SongUtils.dateToString(ZonedDateTime.now()))
            .setProperties(new Properties()
                .setTitles(titles)
                .setAuthors(authors)
                .setCopyright("1982 Jubilate Hymns Limited")
                .setCcliNo(1037882)
                .setThemes(themes)
                .setVerseOrder("v1")
            )
            .setLyrics(verses);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            OpenLyricsWriter writer = new OpenLyricsWriter();
            writer.write(song, baos);
            System.out.println(baos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
