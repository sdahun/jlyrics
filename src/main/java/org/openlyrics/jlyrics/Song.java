package org.openlyrics.jlyrics;

import lombok.Data;
import lombok.experimental.Accessors;
import org.openlyrics.jlyrics.song.ChordNotation;
import org.openlyrics.jlyrics.song.ProcessingInstruction;
import org.openlyrics.jlyrics.song.Properties;
import org.openlyrics.jlyrics.song.SongVersion;
import org.openlyrics.jlyrics.song.format.Tags;
import org.openlyrics.jlyrics.song.lyrics.ILyricsEntry;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.util.SongUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.openlyrics.jlyrics.song.SongConstants.LIB_VERSION;

@Data
@Accessors(chain = true)
public class Song {
    private Integer indent = 2;
    private String newLine = "\n";
    private Boolean doubleQuotesInXmlHeader = false;
    private Boolean multilineSongAttributes = false;
    private List<ProcessingInstruction> processingInstructions = new ArrayList<>();

    private SongVersion version = SongVersion.V0_9;
    private String xmlLang;
    private String createdIn = LIB_VERSION;
    private String modifiedIn = LIB_VERSION;
    private String modifiedDate = SongUtils.dateToString(ZonedDateTime.now());
    private ChordNotation chordNotation;

    private Properties properties = new Properties();
    private List<Tags> format = new ArrayList<>();
    private List<ILyricsEntry> lyrics = new ArrayList<>() {{ add(new Verse()); }};

    public Song getDeepCopy() {
        Song copy = new Song();
        copy.setIndent(indent);
        copy.setNewLine(newLine);
        copy.setDoubleQuotesInXmlHeader(doubleQuotesInXmlHeader);
        copy.setMultilineSongAttributes(multilineSongAttributes);

        for (ProcessingInstruction instruction : processingInstructions) {
            copy.processingInstructions.add(instruction.getDeepCopy());
        }

        copy.setVersion(version);
        copy.setXmlLang(xmlLang);
        copy.setCreatedIn(createdIn);
        copy.setModifiedIn(modifiedIn);
        copy.setModifiedDate(modifiedDate);
        copy.setChordNotation(chordNotation);

        copy.setProperties(properties.getDeepCopy());

        for (Tags tag : format) {
            copy.getFormat().add(tag.getDeepCopy());
        }

        copy.getLyrics().clear();
        for (ILyricsEntry entry : lyrics) {
            copy.getLyrics().add(entry.getDeepCopy());
        }

        return copy;
    }
}
