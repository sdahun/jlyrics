package org.openlyrics.jlyrics.writer;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.lyrics.*;
import org.openlyrics.jlyrics.song.lyrics.linepart.*;
import org.openlyrics.jlyrics.song.properties.*;
import org.openlyrics.jlyrics.util.SongUtils;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.openlyrics.jlyrics.util.SongUtils.encodeNumericEntities;
import static org.openlyrics.jlyrics.util.SongUtils.getVerseTextContent;

public class QueleaSongWriter implements ILyricsWriter {

    //There's no path in qsp file
    @Override
    public String getFilePath(Song song) {
        return "";
    }

    @Override
    public String getFileName(Song song) {
        return SongUtils.removeAccidentals(song.getProperties().getTitles().get(0).getTitle());
    }

    @Override
    public String getFileExtension() {
        return ".xml";
    }

    @Override
    public void write(Song song, OutputStream outputStream) throws Exception {
        StringBuilder xml = new StringBuilder();

        String title = song.getProperties().getTitles().get(0).getTitle();

        String authors = song.getProperties().getAuthors().stream()
            .map(Author::getName)
            .collect(Collectors.joining("; "));

        String ccli = song.getProperties().getCcliNo() != null ? song.getProperties().getCcliNo().toString() : "";

        String copyright = song.getProperties().getCopyright() != null ? song.getProperties().getCopyright() : "";

        String smalllines = title + "\n" + authors + (!ccli.isEmpty() ? " ("+ccli+")" : "") + "\n";

        xml.append("<song>")
            .append("<updateInDB>true</updateInDB>")
            .append("<title>").append(encodeNumericEntities(title)).append("</title>")
            .append("<author>").append(encodeNumericEntities(authors)).append("</author>")
            .append("<ccli>").append(encodeNumericEntities(ccli)).append("</ccli>")
            .append("<copyright>").append(encodeNumericEntities(copyright)).append("</copyright>")
            .append("<year></year>")
            .append("<publisher></publisher>")
            .append("<key></key>")
            .append("<capo></capo>")
            .append("<notes></notes>")
            .append("<sequence></sequence>")
            .append("<lyrics>")
        ;

        for (ILyricsEntry entry : song.getLyrics()) {
            if (entry instanceof Verse) {
                Verse verse = (Verse) entry;

                String lyrics = getVerseTextContent(verse, false);

                xml.append("<section title=\"").append(verse.getFormattedName()).append("\" capitalise=\"true\">")
                    .append("<theme>")
                        .append("fontname:Noto Sans$")
                        .append("translatefontname:Noto Sans$")
                        .append("fontcolour:0xffffffff$")
                        .append("translatefontcolour:0xf5f5f5ff$")
                        .append("isFontBold:true$")
                        .append("isFontItalic:false$")
                        .append("isTranslateFontBold:true$")
                        .append("isTranslateFontItalic:true$")
                        .append("backgroundcolour:0x000000ff$")
                        .append("shadowcolor:0x000000ff$")
                        .append("shadowX:0.0$")
                        .append("shadowY:0.0$")
                        .append("shadowradius:2.0$")
                        .append("shadowspread:0.0$")
                        .append("shadowuse:true$")
                        .append("textposition:-1$")
                        .append("textalignment:0")
                    .append("</theme>")
                    .append("<smalllines>").append(smalllines).append("</smalllines>")
                    .append("<lyrics>").append(lyrics).append("</lyrics>")
                    .append("</section>");
            }
        }

        xml.append("</lyrics>")
            .append("<translation></translation>")
            .append("<translationoptions></translationoptions>")
            .append("</song>")
        ;

        outputStream.write(xml.toString().getBytes(StandardCharsets.UTF_8));
    }
}
