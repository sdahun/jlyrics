package org.openlyrics.jlyrics.reader;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.ChordNotation;
import org.openlyrics.jlyrics.song.ProcessingInstruction;
import org.openlyrics.jlyrics.song.SongVersion;
import org.openlyrics.jlyrics.song.format.Tag;
import org.openlyrics.jlyrics.song.format.Tags;
import org.openlyrics.jlyrics.song.lyrics.*;
import org.openlyrics.jlyrics.song.lyrics.linepart.*;
import org.openlyrics.jlyrics.song.lyrics.linepart.Text;
import org.openlyrics.jlyrics.song.properties.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static org.openlyrics.jlyrics.util.SongUtils.readAllBytes;

public class OpenLyricsReader implements ILyricsReader {
    private final static Map<String, String> entities = new HashMap<>();
    static {
        entities.put("&lt;", "=LT=");
        entities.put("&gt;", "=GT=");
    }

    private Document document;
    private Song song;

    @Override
    public Song read(InputStream inputStream) throws Exception {
        String content = new String(readAllBytes(inputStream), StandardCharsets.UTF_8);

        //determine xml indentation
        this.song = new Song();
        int x = content.indexOf("<properties>");
        int y = content.lastIndexOf("\n", x);
        this.song.setIndent(x-y-1);

        //determine newline character
        song.setNewLine(content.contains("\r\n") ? "\r\n" : "\n");

        //determine double quotes in xml header
        song.setDoubleQuotesInXmlHeader(content.contains("encoding=\""));

        //determine multiLine <song> attributes
        song.setMultilineSongAttributes(content.contains("  version="));

        //replace all &lt; and &gt; to prevent parse them
        content = encodeEntities(content);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try {
            this.document = builder.parse(new InputSource(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.parseDOM();

        return this.song;
    }

    private void parseDOM() {
        this.parseProcessingInstructions();
        NodeList songList = this.document.getElementsByTagName("song");
        if (songList.getLength() == 1) {
            Element elSong = (Element) songList.item(0);
            this.song.setVersion(SongVersion.getVersionByValue(elSong.getAttribute("version")));

            String tmp = elSong.getAttribute("xml:lang");
            this.song.setXmlLang(tmp.isEmpty() ? null : tmp);

            tmp = elSong.getAttribute("createdIn");
            this.song.setCreatedIn(tmp.isEmpty() ? null : tmp);

            tmp = elSong.getAttribute("modifiedIn");
            this.song.setModifiedIn(tmp.isEmpty() ? null : tmp);

            tmp = elSong.getAttribute("modifiedDate");
            this.song.setModifiedDate(tmp.isEmpty() ? null : tmp);

            tmp = elSong.getAttribute("chordNotation");
            this.song.setChordNotation(tmp.isEmpty() ? null : ChordNotation.getChordNotationByName(tmp));

            this.parseProperties();
            this.parseFormat();
            this.parseLyrics();
        }
    }

    private void parseProcessingInstructions() {
        NodeList rootNodes = this.document.getChildNodes();
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Node rootNode = rootNodes.item(i);
            if (rootNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                this.song.getProcessingInstructions().add(
                    new ProcessingInstruction().setName(rootNode.getNodeName()).setValue(rootNode.getNodeValue())
                );
            }
        }
    }

    private void parseProperties() {
        this.parseTitles();
        this.parseAuthors();
        this.parseCopyright();
        this.parseCcliNo();
        this.parseReleased();
        this.parseTransposition();
        this.parseTempo();
        this.parseKey();
        this.parseTimeSignature();
        this.parseVariant();
        this.parsePublisher();
        this.parseVersion();
        this.parseKeywords();
        this.parseVerseOrder();
        this.parseCollection();
        this.parseTrackNo();
        this.parseSongbooks();
        this.parseThemes();
        this.parseComments();
    }

    private void parseTitles() {
        NodeList titlesList = this.document.getElementsByTagName("titles");
        if (titlesList.getLength() == 1) {
            NodeList titleList = titlesList.item(0).getChildNodes();
            if (titleList.getLength() > 0) {
                this.song.getProperties().getTitles().clear();
                for (int i = 0; i < titleList.getLength(); i++) {
                    if (titleList.item(i) instanceof Element) {
                        Element elTitle = (Element) titleList.item(i);
                        Title title = new Title().setTitle(elTitle.getTextContent());

                        if (elTitle.hasAttribute("lang")) {
                            title.setLang(elTitle.getAttribute("lang"));
                        }
                        if (elTitle.hasAttribute("translit")) {
                            title.setTranslit(elTitle.getAttribute("translit"));
                        }
                        if (elTitle.hasAttribute("original")) {
                            title.setOriginal(parseBoolean(elTitle.getAttribute("original")));
                        }

                        this.song.getProperties().getTitles().add(title);
                    }
                }
            }
        }
    }

    private void parseAuthors() {
        NodeList authorsList = this.document.getElementsByTagName("authors");
        if (authorsList.getLength() == 1) {
            NodeList authorList = authorsList.item(0).getChildNodes();
            if (authorList.getLength() > 0) {
                this.song.getProperties().getAuthors().clear();
                for (int i = 0; i < authorList.getLength(); i++) {
                    if (authorList.item(i) instanceof Element) {
                        Element elAuthor = (Element) authorList.item(i);
                        Author author = new Author().setName(elAuthor.getTextContent());

                        if (elAuthor.hasAttribute("type")) {
                            author.setType(AuthorType.valueOf(elAuthor.getAttribute("type").toUpperCase()));
                        }
                        if (elAuthor.hasAttribute("lang")) {
                            author.setLang(elAuthor.getAttribute("lang"));
                        }

                        this.song.getProperties().getAuthors().add(author);
                    }
                }
            }
        }
    }

    private void parseCopyright() {
        NodeList copyrightList = this.document.getElementsByTagName("copyright");
        if (copyrightList.getLength() == 1) {
            this.song.getProperties().setCopyright(copyrightList.item(0).getTextContent());
        }
    }

    private void parseCcliNo() {
        NodeList ccliNoList = this.document.getElementsByTagName("ccliNo");
        if (ccliNoList.getLength() == 1) {
            this.song.getProperties().setCcliNo(parseInt(ccliNoList.item(0).getTextContent()));
        }
    }

    private void parseReleased() {
        NodeList releasedList = this.document.getElementsByTagName("released");
        if (releasedList.getLength() == 1) {
            this.song.getProperties().setReleased(releasedList.item(0).getTextContent());
        }
        else {
            //backward compatibility for v0.6, v0.7
            releasedList = this.document.getElementsByTagName("releaseDate");
            if (releasedList.getLength() == 1) {
                this.song.getProperties().setReleaseDate(releasedList.item(0).getTextContent());
            }
        }
    }

    private void parseTransposition() {
        NodeList transpositionList = this.document.getElementsByTagName("transposition");
        if (transpositionList.getLength() == 1) {
            this.song.getProperties().setTransposition(parseInt(transpositionList.item(0).getTextContent()));
        }
    }

    private void parseTempo() {
        NodeList tempoList = this.document.getElementsByTagName("tempo");
        if (tempoList.getLength() == 1) {
            Element elTempo = (Element) tempoList.item(0);
            Tempo tempo = this.song.getProperties().getTempo();
            tempo.clear();
            if (elTempo.hasAttribute("type")) {
                tempo.setType(TempoType.valueOf(elTempo.getAttribute("type").toUpperCase()));
            }
            String tempoValue = elTempo.getTextContent();
            if (!tempoValue.isEmpty()) {
                tempo.setValue(tempoValue);
            }
        }
    }

    private void parseKey() {
        NodeList keyList = this.document.getElementsByTagName("key");
        if (keyList.getLength() == 1) {
            this.song.getProperties().setKey(keyList.item(0).getTextContent());
        }
    }

    private void parseTimeSignature() {
        NodeList timeSignatureList = this.document.getElementsByTagName("timeSignature");
        if (timeSignatureList.getLength() == 1) {
            TimeSignature signature = new TimeSignature();
            if (signature.parseString(timeSignatureList.item(0).getTextContent())) {
                this.song.getProperties().setTimeSignature(signature);
            }
        }
    }

    private void parseVariant() {
        NodeList variantList = this.document.getElementsByTagName("variant");
        if (variantList.getLength() == 1) {
            this.song.getProperties().setVariant(variantList.item(0).getTextContent());
        }
    }

    private void parsePublisher() {
        NodeList publisherList = this.document.getElementsByTagName("publisher");
        if (publisherList.getLength() == 1) {
            this.song.getProperties().setPublisher(publisherList.item(0).getTextContent());
        }
    }

    private void parseVersion() {
        NodeList versionList = this.document.getElementsByTagName("version");
        if (versionList.getLength() == 1) {
            this.song.getProperties().setVersion(versionList.item(0).getTextContent());
        }
        else {
            //backward compatibility for v0.6, v0.7
            versionList = this.document.getElementsByTagName("customVersion");
            if (versionList.getLength() == 1) {
                this.song.getProperties().setCustomVersion(versionList.item(0).getTextContent());
            }
        }
    }

    private void parseKeywords() {
        NodeList keywordsList = this.document.getElementsByTagName("keywords");
        if (keywordsList.getLength() == 1) {
            this.song.getProperties().setKeywords(keywordsList.item(0).getTextContent());
        }
    }

    private void parseVerseOrder() {
        NodeList verseOrderList = this.document.getElementsByTagName("verseOrder");
        if (verseOrderList.getLength() == 1) {
            this.song.getProperties().setVerseOrder(verseOrderList.item(0).getTextContent());
        }
    }

    private void parseCollection() {
        NodeList collectionList = this.document.getElementsByTagName("collection");
        if (collectionList.getLength() == 1) {
            this.song.getProperties().setCollection(collectionList.item(0).getTextContent());
        }
    }

    private void parseTrackNo() {
        NodeList trackNoList = this.document.getElementsByTagName("trackNo");
        if (trackNoList.getLength() == 1) {
            this.song.getProperties().setTrackNo(parseInt(trackNoList.item(0).getTextContent()));
        }
    }

    private void parseSongbooks() {
        NodeList songbooksList = this.document.getElementsByTagName("songbooks");
        if (songbooksList.getLength() == 1) {
            NodeList songbookList = songbooksList.item(0).getChildNodes();
            if (songbookList.getLength() > 0) {
                this.song.getProperties().getSongbooks().clear();
                for (int i = 0; i < songbookList.getLength(); i++) {
                    if (songbookList.item(i) instanceof Element) {
                        Element elSongbook = (Element) songbookList.item(i);
                        Songbook songbook = new Songbook();

                        if (elSongbook.hasAttribute("name")) {
                            songbook.setName(elSongbook.getAttribute("name"));
                        }
                        if (elSongbook.hasAttribute("entry")) {
                            songbook.setEntry(elSongbook.getAttribute("entry"));
                        }

                        this.song.getProperties().getSongbooks().add(songbook);
                    }
                }
            }
        }
    }

    private void parseThemes() {
        NodeList themesList = this.document.getElementsByTagName("themes");
        if (themesList.getLength() == 1) {
            NodeList themeList = themesList.item(0).getChildNodes();
            if (themeList.getLength() > 0) {
                this.song.getProperties().getThemes().clear();
                for (int i = 0; i < themeList.getLength(); i++) {
                    if (themeList.item(i) instanceof Element) {
                        Element elTheme = (Element) themeList.item(i);
                        Theme theme = new Theme().setTheme(elTheme.getTextContent());

                        if (elTheme.hasAttribute("id")) {
                            theme.setCcliThemeId(parseInt(elTheme.getAttribute("id")));
                        }
                        if (elTheme.hasAttribute("lang")) {
                            theme.setLang(elTheme.getAttribute("lang"));
                        }
                        if (elTheme.hasAttribute("translit")) {
                            theme.setTranslit(elTheme.getAttribute("translit"));
                        }

                        this.song.getProperties().getThemes().add(theme);
                    }
                }
            }
        }
    }

    private void parseComments() {
        NodeList commentsList = this.document.getElementsByTagName("comments");
        if (commentsList.getLength() == 1) {
            NodeList commentList = commentsList.item(0).getChildNodes();
            if (commentList.getLength() > 0) {
                this.song.getProperties().getComments().clear();
                for (int i = 0; i < commentList.getLength(); i++) {
                    if (commentList.item(i) instanceof Element) {
                        Element elComment = (Element) commentList.item(i);
                        this.song.getProperties().getComments().add(elComment.getTextContent());
                    }
                }
            }
        }
    }

    private void parseFormat() {
        NodeList formatList = this.document.getElementsByTagName("format");
        if (formatList.getLength() == 1) {
            NodeList tagsList = formatList.item(0).getChildNodes();
            if (tagsList.getLength() > 0) {
                this.song.setFormat(new ArrayList<>());
                for (int i = 0; i < tagsList.getLength(); i++) {
                    if (tagsList.item(i) instanceof Element) {
                        Element elTags = (Element) tagsList.item(i);
                        Tags tags = new Tags();
                        tags.getEntries().clear();
                        tags.setApplication(elTags.getAttribute("application"));
                        this.song.getFormat().add(tags);

                        NodeList tagList = elTags.getChildNodes();
                        for (int j = 0; j < tagList.getLength(); j++) {
                            if (tagList.item(j) instanceof Element) {
                                Element elTag = (Element) tagList.item(j);
                                Tag tag = new Tag();
                                tag.setName(elTag.getAttribute("name"));
                                tags.getEntries().add(tag);

                                NodeList entryList = elTag.getChildNodes();
                                for (int k = 0; k < entryList.getLength(); k++) {
                                    if (entryList.item(k) instanceof Element) {
                                        Element elEntry = (Element) entryList.item(k);
                                        if ("open".equals(elEntry.getTagName())) {
                                            tag.setOpen(decodeEntities(elEntry.getTextContent()));
                                        }
                                        if ("close".equals(elEntry.getTagName())) {
                                            tag.setClose(decodeEntities(elEntry.getTextContent()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseLyrics() {
        NodeList lyricsList = this.document.getElementsByTagName("lyrics");
        if (lyricsList.getLength() == 1) {
            //entries can be verses or instruments
            NodeList lyricsEntryList = lyricsList.item(0).getChildNodes();
            if (lyricsEntryList.getLength() > 0) {
                this.parseLyricsEntries(lyricsEntryList);
            }
        }
    }

    private void parseLyricsEntries(NodeList lyricsEntryList) {
        //clear default entries
        this.song.getLyrics().clear();

        for (int i = 0; i < lyricsEntryList.getLength(); i++) {
            if (lyricsEntryList.item(i) instanceof Element) {
                Element elEntry = (Element) lyricsEntryList.item(i);
                switch (elEntry.getTagName()) {
                    case "verse":
                        this.parseVerse(elEntry);
                        break;
                    case "instrument":
                        this.parseInstrument(elEntry);
                        break;
                }
            }
        }
    }

    private void parseVerse(Element elVerse) {
        Verse verse = new Verse();
        verse.setName(elVerse.getAttribute("name"));

        if (elVerse.hasAttribute("lang")) {
            verse.setLang(elVerse.getAttribute("lang"));
        }

        if (elVerse.hasAttribute("translit")) {
            verse.setTranslit(elVerse.getAttribute("translit"));
        }

        NodeList linesList = elVerse.getChildNodes();
        if (linesList.getLength() > 0) {
            verse.getLines().clear();
            for (int i = 0; i < linesList.getLength(); i++) {
                if (linesList.item(i) instanceof Element) {
                    Element elLine = (Element) linesList.item(i);
                    this.parseVerseLine(verse, elLine);
                }
            }
        }

        this.song.getLyrics().add(verse);
    }

    private void parseVerseLine(Verse verse, Element elLine) {
        VerseLine line = new VerseLine();

        if (elLine.hasAttribute("part")) {
            line.setPart(elLine.getAttribute("part"));
        }

        if (elLine.hasAttribute("break")) {
            line.setOptionalBreak(elLine.getAttribute("break"));
        }

        if (elLine.hasAttribute("repeat")) {
            line.setRepeat(parseInt(elLine.getAttribute("repeat")));
        }

        NodeList lineParts = elLine.getChildNodes();
        if (lineParts.getLength() > 0) {
            for (int i = 0; i < lineParts.getLength(); i++) {
                this.parseVerseLinePart(line, lineParts.item(i));
            }
        }

        verse.getLines().add(line);
    }

    private void parseVerseLinePart(MixedContainer line, Node elPart) {
        if (elPart.getNodeType() == Node.COMMENT_NODE) {
            return;
        }

        if (elPart.getNodeType() == Node.TEXT_NODE) {
            //check if the Node is an empty, newline textnode
            if (elPart.getTextContent().contains("\n") && elPart.getTextContent().trim().isEmpty()) {
                if (line.getParts().isEmpty()) {
                    line.setMultiLine(true);
                } else {
                    ((MixedContainer)line.getParts().get(line.getParts().size()-1)).setMultiLine(true);
                }
            }
            else {
                line.getParts().add(new Text().setContent(decodeEntities(elPart.getTextContent())));
            }
        }

        else
        if (elPart.getNodeName().equals("chord")) {
            Element elChord = (Element) elPart;
            Chord chord = new Chord();

            //v0.8
            if (elChord.hasAttribute("name")) {
                chord.setName(elChord.getAttribute("name"));
            }

            //v0.9
            if (elChord.hasAttribute("root")) {
                chord.setRoot(MusicalNote.getNoteByName(elChord.getAttribute("root")));
            }

            if (elChord.hasAttribute("bass")) {
                chord.setBass(MusicalNote.getNoteByName(elChord.getAttribute("bass")));
            }
            if (elChord.hasAttribute("structure")) {
                chord.setStructure(elChord.getAttribute("structure"));
            }
            if (elChord.hasAttribute("upbeat")) {
                chord.setUpbeat(parseBoolean(elChord.getAttribute("upbeat")));
            }

            NodeList subParts = elChord.getChildNodes();
            if (subParts.getLength() > 0) {
                for (int i = 0; i < subParts.getLength(); i++) {
                    parseVerseLinePart(chord, subParts.item(i));
                }
            }

            line.getParts().add(chord);
        }

        else {
            LineTag tag = new LineTag().setName(elPart.getNodeName());

            NamedNodeMap elAttributes = elPart.getAttributes();
            for (int i = 0; i < elAttributes.getLength(); i++) {
                Node attribute = elAttributes.item(i);
                tag.getProperties().put(attribute.getNodeName(), attribute.getNodeValue());
            }

            NodeList subParts = elPart.getChildNodes();
            if (subParts.getLength() > 0) {
                for (int i = 0; i < subParts.getLength(); i++) {
                    parseVerseLinePart(tag, subParts.item(i));
                }
            }

            line.getParts().add(tag);
        }
    }

    private void parseInstrument(Element elInstrument) {
        Instrument instrument = new Instrument();
        instrument.setName(elInstrument.getAttribute("name"));

        NodeList linesList = elInstrument.getChildNodes();
        if (linesList.getLength() > 0) {
            instrument.getLines().clear();
            for (int i = 0; i < linesList.getLength(); i++) {
                if (linesList.item(i) instanceof Element) {
                    Element elLine = (Element) linesList.item(i);
                    this.parseInstrumentLine(instrument, elLine);
                }
            }
        }

        this.song.getLyrics().add(instrument);
    }

    private void parseInstrumentLine(Instrument instrument, Element elLine) {
        InstrumentLine line = new InstrumentLine();

        if (elLine.hasAttribute("repeat")) {
            line.setRepeat(parseInt(elLine.getAttribute("repeat")));
        }

        NodeList lineParts = elLine.getChildNodes();
        if (lineParts.getLength() > 0) {
            for (int i = 0; i < lineParts.getLength(); i++) {
                this.parseInstrumentLinePart(line, lineParts.item(i));
            }
        }

        instrument.getLines().add(line);
    }

    private void parseInstrumentLinePart(MixedContainer line, Node elPart) {
        if (elPart.getNodeName().equals("beat")) {
            Element elBeat = (Element) elPart;
            Beat beat = new Beat();

            NodeList subParts = elBeat.getChildNodes();
            if (subParts.getLength() > 0) {
                for (int i = 0; i < subParts.getLength(); i++) {
                    parseInstrumentLinePart(beat, subParts.item(i));
                }
            }

            line.getParts().add(beat);
        }
        else

        if (elPart.getNodeName().equals("chord")) {
            Element elChord = (Element) elPart;
            Chord chord = new Chord();

            //v0.8
            if (elChord.hasAttribute("name")) {
                chord.setName(elChord.getAttribute("name"));
            }

            //v0.9
            if (elChord.hasAttribute("root")) {
                chord.setRoot(MusicalNote.getNoteByName(elChord.getAttribute("root")));
            }

            if (elChord.hasAttribute("bass")) {
                chord.setBass(MusicalNote.getNoteByName(elChord.getAttribute("bass")));
            }
            if (elChord.hasAttribute("structure")) {
                chord.setStructure(elChord.getAttribute("structure"));
            }
            if (elChord.hasAttribute("upbeat")) {
                chord.setUpbeat(parseBoolean(elChord.getAttribute("upbeat")));
            }

            NodeList subParts = elChord.getChildNodes();
            if (subParts.getLength() > 0) {
                for (int i = 0; i < subParts.getLength(); i++) {
                    parseInstrumentLinePart(chord, subParts.item(i));
                }
            }

            line.getParts().add(chord);
        }
    }

    private static String encodeEntities(String source) {
        for (Map.Entry<String, String> entity: entities.entrySet()) {
            source = source.replaceAll(entity.getKey(), entity.getValue());
        }
        return source;
    }

    private static String decodeEntities(String source) {
        for (Map.Entry<String, String> entity: entities.entrySet()) {
            source = source.replaceAll(entity.getValue(), entity.getKey());
        }
        return source;
    }
}
