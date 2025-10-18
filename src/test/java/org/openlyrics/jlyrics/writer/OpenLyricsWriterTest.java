package org.openlyrics.jlyrics.writer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.SongVersion;
import org.openlyrics.jlyrics.song.properties.Theme;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openlyrics.jlyrics.song.SongVersion.*;
import static org.openlyrics.jlyrics.validation.SchemaValidator.validateXmlToSchema;

class OpenLyricsWriterTest {

    @Test
    void testEmptySongIsValid() throws Exception {
        Song emptySong = new Song();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOFactory.getNewWriter(WriterType.OPENLYRICS).write(emptySong, outputStream);

        ByteArrayInputStream xmlStream = new ByteArrayInputStream(outputStream.toByteArray());
        InputStream schemaStream = this.getSchemaStreamBySongVersion(emptySong.getVersion());

        validateXmlToSchema(xmlStream, schemaStream);
    }

/*
    @Test
    void testSaveEmptySongToFile() throws Exception {
        Song emptySong = new Song();
        OutputStream outputStream = new FileOutputStream("result.xml");
        IOFactory.getNewWriter(WriterType.XML).write(emptySong, outputStream);
    }

    @Test
    void testSaveLyricsToOpenLyricsFormat() throws Exception {
        OutputStream outputStream = new FileOutputStream("result.xml");
        IOFactory.getNewWriter(WriterType.XML).write(this.song, outputStream);
    }
*/

    @ParameterizedTest
    @MethodSource("provideCcliThemeIdByVersion")
    void testCcliThemeIdByVersion(SongVersion version, boolean hasCcliThemeId) throws Exception {
        Song song = new Song();
        song.setVersion(version);
        song.getProperties().getThemes().add(new Theme().setTheme("Adoration").setCcliThemeId(123));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOFactory.getNewWriter(WriterType.OPENLYRICS).write(song, outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(inputStream));
        NodeList themeTags = document.getElementsByTagName("theme");

        assertEquals(hasCcliThemeId, ((Element)themeTags.item(0)).hasAttribute("id"));
    }

    static Stream<Arguments> provideCcliThemeIdByVersion() {
        return Stream.of(
                Arguments.of(V0_6, true),
                Arguments.of(V0_7, true),
                Arguments.of(V0_8, false),
                Arguments.of(V0_9, false)
        );
    }

    InputStream getSchemaStreamBySongVersion(SongVersion songVersion) {
        return this.getClass().getClassLoader().getResourceAsStream(String.format("schema/openlyrics-%s.rng", songVersion.getValue()));
    }
}
