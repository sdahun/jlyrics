package org.openlyrics.jlyrics.writer;

import lombok.Data;
import lombok.experimental.Accessors;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.ProcessingInstruction;
import org.openlyrics.jlyrics.song.format.Tag;
import org.openlyrics.jlyrics.song.format.Tags;
import org.openlyrics.jlyrics.song.lyrics.*;
import org.openlyrics.jlyrics.song.lyrics.linepart.*;
import org.openlyrics.jlyrics.song.properties.*;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.openlyrics.jlyrics.song.SongVersion.*;
import static org.openlyrics.jlyrics.util.SongUtils.repeat;

public class OpenLyricsWriter implements ILyricsWriter {
    private Song song;

    @Override
    public String getFileExtension() {
        return ".xml";
    }

    @Override
    public void write(Song song, OutputStream outputStream) throws Exception {
        this.song = song;

        String songXml = getXmlHeader() + getXmlBody() + song.getNewLine();
        outputStream.write(songXml.getBytes(StandardCharsets.UTF_8));
    }

    private String getXmlHeader() {
        StringBuilder header = new StringBuilder();

        String xmlHeader = "<?xml version='1.0' encoding='utf-8'?>";
        if (song.getDoubleQuotesInXmlHeader()) {
            xmlHeader = xmlHeader.replaceAll("'", "\"");
        }

        header.append(xmlHeader).append(song.getNewLine());

        for (ProcessingInstruction pi : song.getProcessingInstructions()) {
            header.append("<?").append(pi.getName()).append(" ").append(pi.getValue()).append("?>").append(song.getNewLine());
        }

        return header.toString();
    }

    private String getXmlBody() {
        //setup newline once and for all
        XmlTag.newLine = song.getNewLine();

        XmlTag songTag = new XmlTag("song").setMultilineAttributes(song.getMultilineSongAttributes());

        songTag.addAttribute("xmlns", "http://openlyrics.info/namespace/2009/song");

        if (song.getXmlLang() != null) {
            songTag.addAttribute("xml:lang", song.getXmlLang());
        }
        songTag.addAttribute("version", song.getVersion().getValue());

        if (song.getCreatedIn() != null) {
            songTag.addAttribute("createdIn", song.getCreatedIn());
        }
        if (song.getModifiedIn() != null) {
            songTag.addAttribute("modifiedIn", song.getModifiedIn());
        }
        if (song.getModifiedDate() != null) {
            songTag.addAttribute("modifiedDate", song.getModifiedDate());
        }
        if (song.getChordNotation() != null) {
            songTag.addAttribute("chordNotation", song.getChordNotation().getName());
        }

        songTag.addChild(getPropertiesTag());

        if ((this.song.getVersion().compareTo(V0_8) >= 0) && (!this.song.getFormat().isEmpty())) {
            songTag.addChild(getFormatTag());
        }

        songTag.addChild(getLyricsTag());
        return songTag.getStringContent(song.getIndent());
    }

    private XmlTag getPropertiesTag() {
        XmlTag propertiesTag = new XmlTag("properties");

        propertiesTag.addChild(getTitlesTag());

        if (!song.getProperties().getAuthors().isEmpty()) {
            propertiesTag.addChild(getAuthorsTag());
        }

        if (song.getProperties().getCopyright() != null) {
            XmlTag copyrightTag = new XmlTag("copyright", false);
            copyrightTag.addChild(new XmlTag().setContent(song.getProperties().getCopyright()));
            propertiesTag.addChild(copyrightTag);
        }

        if (song.getProperties().getCcliNo() != null) {
            XmlTag ccliNoTag = new XmlTag("ccliNo", false);
            ccliNoTag.addChild(new XmlTag().setContent(song.getProperties().getCcliNo().toString()));
            propertiesTag.addChild(ccliNoTag);
        }

        //version dependent tag name: until v0.7: releaseDate, v0.8+: released
        if (song.getProperties().getReleased() != null) {
            String tagName = V0_8.compareTo(song.getVersion()) > 0 ? "releaseDate" : "released";
            XmlTag releasedTag = new XmlTag(tagName, false);
            releasedTag.addChild(new XmlTag().setContent(song.getProperties().getReleased()));
            propertiesTag.addChild(releasedTag);
        }

        if (song.getProperties().getTransposition() != null) {
            XmlTag transpositionTag = new XmlTag("transposition", false);
            transpositionTag.addChild(new XmlTag().setContent(song.getProperties().getTransposition().toString()));
            propertiesTag.addChild(transpositionTag);
        }

        if (!song.getProperties().getTempo().isEmpty()) {
            propertiesTag.addChild(getTempoTag());
        }

        if (song.getProperties().getKey() != null) {
            XmlTag keyTag = new XmlTag("key", false);
            keyTag.addChild(new XmlTag().setContent(song.getProperties().getKey()));
            propertiesTag.addChild(keyTag);
        }

        if ((song.getVersion().compareTo(V0_9) >= 0) && (song.getProperties().getTimeSignature() != null)) {
            XmlTag timeSignatureTag = new XmlTag("timeSignature", false);
            timeSignatureTag.addChild(new XmlTag().setContent(song.getProperties().getTimeSignature().toString()));
            propertiesTag.addChild(timeSignatureTag);
        }

        if (song.getProperties().getVariant() != null) {
            XmlTag variantTag = new XmlTag("variant", false);
            variantTag.addChild(new XmlTag().setContent(song.getProperties().getVariant()));
            propertiesTag.addChild(variantTag);
        }

        if (song.getProperties().getPublisher() != null) {
            XmlTag publisherTag = new XmlTag("publisher", false);
            publisherTag.addChild(new XmlTag().setContent(song.getProperties().getPublisher()));
            propertiesTag.addChild(publisherTag);
        }

        //version dependent tag name: until v0.7: customVersion, v0.8+: version
        if (song.getProperties().getVersion() != null) {
            String tagName = V0_8.compareTo(song.getVersion()) > 0 ? "customVersion" : "version";
            XmlTag versionTag = new XmlTag(tagName, false);
            versionTag.addChild(new XmlTag().setContent(song.getProperties().getVersion()));
            propertiesTag.addChild(versionTag);
        }

        if (song.getProperties().getKeywords() != null) {
            XmlTag keywordsTag = new XmlTag("keywords", false);
            keywordsTag.addChild(new XmlTag().setContent(song.getProperties().getKeywords()));
            propertiesTag.addChild(keywordsTag);
        }

        if (song.getProperties().getVerseOrder() != null) {
            XmlTag verseOrderTag = new XmlTag("verseOrder", false);
            verseOrderTag.addChild(new XmlTag().setContent(song.getProperties().getVerseOrder()));
            propertiesTag.addChild(verseOrderTag);
        }

        //v0.6 only element
        if (song.getProperties().getCollection() != null && song.getVersion() == V0_6) {
            XmlTag collectionTag = new XmlTag("collection", false);
            collectionTag.addChild(new XmlTag().setContent(song.getProperties().getCollection()));
            propertiesTag.addChild(collectionTag);
        }

        //v0.6 only element
        if (song.getProperties().getTrackNo() != null && song.getVersion() == V0_6) {
            XmlTag trackNoTag = new XmlTag("trackNo", false);
            trackNoTag.addChild(new XmlTag().setContent(song.getProperties().getTrackNo().toString()));
            propertiesTag.addChild(trackNoTag);
        }

        //v0.7+ element
        if (!song.getProperties().getSongbooks().isEmpty() && song.getVersion().compareTo(V0_7) >= 0) {
            propertiesTag.addChild(getSongbooksTag());
        }

        if (!song.getProperties().getThemes().isEmpty()) {
            propertiesTag.addChild(getThemesTag());
        }

        if (!song.getProperties().getComments().isEmpty()) {
            propertiesTag.addChild(getCommentsTag());
        }

        return propertiesTag;
    }

    private XmlTag getTitlesTag() {
        XmlTag titlesTag = new XmlTag("titles");
        for (Title title : song.getProperties().getTitles()) {
            XmlTag titleTag = new XmlTag("title", false);
            if (title.getOriginal() != null && title.getOriginal()) {
                titleTag.addAttribute("original", title.getOriginal().toString());
            }
            if (title.getLang() != null) {
                titleTag.addAttribute("lang", title.getLang());
            }
            if (title.getTranslit() != null) {
                titleTag.addAttribute("translit", title.getTranslit());
            }
            titleTag.addChild(new XmlTag().setContent(title.getTitle()));
            titlesTag.addChild(titleTag);
        }
        return titlesTag;
    }

    private XmlTag getAuthorsTag() {
        XmlTag authorsTag = new XmlTag("authors");

        for (Author author : song.getProperties().getAuthors()) {
            XmlTag authorTag = new XmlTag("author", false);
            if (author.getType() != null) {
                authorTag.addAttribute("type", author.getType().getName());
            }
            if (author.getLang() != null) {
                authorTag.addAttribute("lang", author.getLang());
            }
            authorTag.addChild(new XmlTag().setContent(author.getName()));
            authorsTag.addChild(authorTag);
        }
        return authorsTag;
    }

    private XmlTag getTempoTag() {
        XmlTag tempoTag = new XmlTag("tempo", false);
        Tempo tempo = song.getProperties().getTempo();
        if (tempo.getType() != null) {
            tempoTag.addAttribute("type", tempo.getType().toString().toLowerCase());
        }
        if (tempo.getValue() != null) {
            tempoTag.addChild(new XmlTag().setContent(tempo.getValue()));
        }
        return tempoTag;
    }

    private XmlTag getSongbooksTag() {
        XmlTag songbooksTag = new XmlTag("songbooks");
        for (Songbook book: song.getProperties().getSongbooks()) {
            XmlTag bookTag = new XmlTag("songbook", false);
            if (book.getName() != null) {
                bookTag.addAttribute("name", book.getName());
            }
            if (book.getEntry() != null) {
                bookTag.addAttribute("entry", book.getEntry());
            }
            songbooksTag.addChild(bookTag);
        }
        return songbooksTag;
    }

    private XmlTag getThemesTag() {
        XmlTag themesTag = new XmlTag("themes");
        for (Theme theme: song.getProperties().getThemes()){
            XmlTag themeTag = new XmlTag("theme", false);

            if (theme.getCcliThemeId() != null && V0_8.compareTo(song.getVersion()) > 0) {
                themeTag.addAttribute("id", theme.getCcliThemeId().toString());
            }

            if (theme.getLang() != null) {
                themeTag.addAttribute("lang", theme.getLang());
            }

            if (theme.getTranslit() != null) {
                themeTag.addAttribute("translit", theme.getTranslit());
            }

            themeTag.addChild(new XmlTag().setContent(theme.getTheme()));

            themesTag.addChild(themeTag);
        }
        return themesTag;
    }

    private XmlTag getCommentsTag() {
        XmlTag commentsTag = new XmlTag("comments");
        for (String comment: song.getProperties().getComments()) {
            XmlTag commentTag = new XmlTag("comment", false);
            commentTag.addChild(new XmlTag().setContent(comment));
            commentsTag.addChild(commentTag);
        }
        return commentsTag;
    }

    private XmlTag getFormatTag() {
        XmlTag formatTag = new XmlTag("format");
        for (Tags tags : song.getFormat()) {
            XmlTag tagsTag = new XmlTag("tags");
            tagsTag.addAttribute("application", tags.getApplication());

            for (Tag tag : tags.getEntries()) {
                XmlTag tagTag = new XmlTag("tag");
                tagTag.addAttribute("name", tag.getName());

                XmlTag openTag = new XmlTag("open", false);
                openTag.addChild(new XmlTag().setContent(tag.getOpen()));
                tagTag.addChild(openTag);

                if (tag.getClose() != null) {
                    XmlTag closeTag = new XmlTag("close", false);
                    closeTag.addChild(new XmlTag().setContent(tag.getClose()));
                    tagTag.addChild(closeTag);
                }

                tagsTag.addChild(tagTag);
            }

            formatTag.addChild(tagsTag);
        }
        return formatTag;
    }

    private XmlTag getLyricsTag() {
        XmlTag lyricsTag = new XmlTag("lyrics");
        for (ILyricsEntry lyricsEntry: song.getLyrics()) {
            if (lyricsEntry instanceof Verse) {
                Verse verse = (Verse) lyricsEntry;
                lyricsTag.addChild(getVerseTag(verse));
            } else
            if (song.getVersion().compareTo(V0_9) >= 0 && lyricsEntry instanceof Instrument) {
                Instrument instrument = (Instrument) lyricsEntry;
                lyricsTag.addChild(getInstrumentTag(instrument));
            }
        }
        return lyricsTag;
    }

    private XmlTag getVerseTag(Verse verse) {
        XmlTag verseTag = new XmlTag("verse");
        verseTag.addAttribute("name", verse.getName());

        if (verse.getLang() != null) {
            verseTag.addAttribute("lang", verse.getLang());
        }
        if (verse.getTranslit() != null) {
            verseTag.addAttribute("translit", verse.getTranslit());
        }

        for (VerseLine line: verse.getLines()) {
            XmlTag linesTag = new XmlTag("lines", line.getMultiLine());

            if (line.getPart() != null) {
                linesTag.addAttribute("part", line.getPart());
            }

            if (line.getOptionalBreak() != null) {
                linesTag.addAttribute("break", line.getOptionalBreak());
            }

            if (line.getRepeat() != null) {
                linesTag.addAttribute("repeat", line.getRepeat().toString());
            }

            addSubLineParts(linesTag, line.getParts());

            verseTag.addChild(linesTag);
        }

        return verseTag;
    }

    private void addSubLineParts(XmlTag parentTag, List<ILinePart> parts) {
        //backward compatibility: if <lines> is empty, write a required <line> tag according to v0.6 and v0.7 schema
        if (parts.isEmpty() && song.getVersion().compareTo(V0_8) < 0) {
            parentTag.addChild(new XmlTag("line"));
        } else {
            for (ILinePart part: parts) {
                if (part instanceof Text) {
                    Text text = (Text) part;
                    parentTag.addChild(new XmlTag().setMultiline(false).setContent(text.getContent()));
                } else
                if (part instanceof Chord) {
                    Chord chord = (Chord) part;
                    XmlTag chordTag = new XmlTag("chord", chord.getMultiLine());
                    if (song.getVersion() == V0_8) {
                        chordTag.addAttribute("name", chord.getName());
                    } else
                    if (song.getVersion() == V0_9) {
                        chordTag.addAttribute("root", chord.getRoot().getName());

                        if (chord.getStructure() != null) {
                            chordTag.addAttribute("structure", chord.getStructure());
                        }
                        if (chord.getBass() != null) {
                            chordTag.addAttribute("bass", chord.getBass().getName());
                        }
                        if (chord.getUpbeat() != null) {
                            chordTag.addAttribute("upbeat", chord.getUpbeat().toString());
                        }

                        if (!chord.getParts().isEmpty()) {
                            addSubLineParts(chordTag, chord.getParts());
                        }
                    }
                    parentTag.addChild(chordTag);
                } else
                if (part instanceof LineTag) {
                    LineTag tag = (LineTag) part;
                    XmlTag tagTag = new XmlTag(tag.getName(), false);
                    for (Map.Entry<String, String> attr: tag.getProperties().entrySet()) {
                        tagTag.addAttribute(attr.getKey(), attr.getValue());
                    }

                    if (!tag.getParts().isEmpty()) {
                        addSubLineParts(tagTag, tag.getParts());
                    }
                    parentTag.addChild(tagTag);
                }
            }
        }
    }

    private XmlTag getInstrumentTag(Instrument instrument) {
        XmlTag instrumentTag = new XmlTag("instrument");
        instrumentTag.getAttributes().put("name", instrument.getName());

        for (InstrumentLine line: instrument.getLines()) {
            XmlTag lineTag = new XmlTag("lines");
            if (line.getRepeat() != null) {
                lineTag.getAttributes().put("repeat", line.getRepeat().toString());
            }
            addSubInstrumentLineParts(lineTag, line.getParts());
            instrumentTag.addChild(lineTag);
        }

        return instrumentTag;
    }

    private void addSubInstrumentLineParts(XmlTag parentTag, List<ILinePart> parts) {
        for (ILinePart part: parts) {
            if (part instanceof Beat) {
                Beat beat = (Beat) part;
                XmlTag beatTag = new XmlTag("beat", false);
                if (!beat.getParts().isEmpty()) {
                    addSubInstrumentLineParts(beatTag, beat.getParts());
                }
                parentTag.addChild(beatTag);
            } else if (part instanceof Chord) {
                Chord chord = (Chord) part;
                XmlTag chordTag = new XmlTag("chord", false);
                chordTag.addAttribute("root", chord.getRoot().getName());

                if (chord.getStructure() != null) {
                    chordTag.addAttribute("structure", chord.getStructure());
                }
                if (chord.getBass() != null) {
                    chordTag.addAttribute("bass", chord.getBass().getName());
                }
                if (chord.getUpbeat() != null) {
                    chordTag.addAttribute("upbeat", chord.getUpbeat().toString());
                }

                if (!chord.getParts().isEmpty()) {
                    addSubLineParts(chordTag, chord.getParts());
                }

                parentTag.addChild(chordTag);
            }
        }
    }

    @Data
    @Accessors(chain = true)
    private static class XmlTag {
        private static String newLine = "\n";

        private int indent = 0;
        private boolean multiline = true;
        private String tagName = "";

        private boolean multilineAttributes = false;
        private Map<String, String> attributes = new LinkedHashMap<>();

        private List<XmlTag> children = new ArrayList<>();
        private String content = "";

        public XmlTag() {}

        public XmlTag(String tagName) {
            this.tagName = tagName;
        }

        public XmlTag(String tagName, boolean multiline) {
            this.tagName = tagName;
            this.multiline = multiline;
        }

        public void addChild(XmlTag child) {
            this.children.add(child);
        }

        public void addAttribute(String name, String value) {
            this.attributes.put(name, value);
        }

        public String getStringContent(int baseIndent) {
            children.forEach(x -> x.setIndent(indent + baseIndent));
            StringBuilder str = new StringBuilder();

            if (!tagName.isEmpty()) {

                str.append("<");
                str.append(tagName);

                if(!attributes.isEmpty()) {
                    boolean firstAttribute = true;
                    for (Map.Entry<String, String> elem: attributes.entrySet()) {
                        if(multilineAttributes && !firstAttribute) {
                            str.append(newLine).append(repeat(" ", indent + (2 * baseIndent)));
                        } else {
                            str.append(" ");
                        }
                        str.append(elem.getKey()).append("=\"").append(elem.getValue()).append("\"");
                        firstAttribute = false;
                    }
                }

                if (!children.isEmpty()) {
                    str.append(">");
                    for (XmlTag child: children) {
                        if (multiline) {
                            str.append(newLine).append(repeat(" ", indent + baseIndent));
                        }
                        str.append(child.getStringContent(baseIndent));
                    }
                    if (multiline) {
                        str.append(newLine).append(repeat(" ", indent));
                    }
                    str.append("</").append(tagName).append(">");
                }
                else {
                    str.append("/>");
                }
            }
            else if (!content.isEmpty()) {
                str.append(content);
            }
            return str.toString();
        }
    }
}
