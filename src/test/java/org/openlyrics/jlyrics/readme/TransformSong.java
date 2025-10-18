package org.openlyrics.jlyrics.readme;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.properties.Songbook;
import org.openlyrics.jlyrics.transform.ConfigSongBookData;
import org.openlyrics.jlyrics.transform.SongTransformer;
import org.openlyrics.jlyrics.transform.SongTransformerConfig;
import org.openlyrics.jlyrics.writer.OpenLyricsWriter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class TransformSong {
    public static void main(String[] args) {

        Song song = new Song();
        song.getProperties()
            .setSongbooks(new ArrayList<>() {{
                add(new Songbook().setName("Hymns and Praises").setEntry("123"));
            }})
            .setVerseOrder("v1");

        SongTransformerConfig config = new SongTransformerConfig()
                .setIntroSlide(true)
                .setIntroSongBook(true)
                .setIntroSongNumber(true)

                .setLineBreak(true)
                .setFirstUppercase(true)
                .setRepeatVerses(false)

                .setEmptySlide(true)
                .setTagSlide(true)

                .addSongbookData(new ConfigSongBookData("HP", "Hymns and Praises", "hymns_and_praises"));

        song = new SongTransformer().transform(song, config);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            OpenLyricsWriter writer = new OpenLyricsWriter();
            writer.write(song, baos);
            System.out.println(baos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
