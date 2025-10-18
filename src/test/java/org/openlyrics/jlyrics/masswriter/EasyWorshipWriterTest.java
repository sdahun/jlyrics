package org.openlyrics.jlyrics.masswriter;
/*
import org.junit.jupiter.api.Test;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.reader.ReaderType;
*/

import org.junit.jupiter.api.Test;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EasyWorshipWriterTest {

    @Test
    void testContentZipping() throws DataFormatException {
        String originalContent = "This is the CONTENT!!!This is the CONTENT!!!";
        EasyWorshipWriter.EasyWorshipEntry entry = new EasyWorshipWriter.EasyWorshipEntry().setContent(originalContent);

        Inflater inflater = new Inflater();
        inflater.setInput(entry.getZippedContent());
        byte[] result = new byte[entry.getOriginalContentSize()];
        inflater.inflate(result);
        inflater.end();

        assertEquals(originalContent, new String(result));
    }

/*
    @Test
    void testEasyWorshipWriter() throws Exception {
        InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("songs/original/Amazing Grace.xml");
        InputStream xml2Stream = this.getClass().getClassLoader().getResourceAsStream("songs/original/Great Is Thy Faithfulness.xml");
        if (xmlStream != null && xml2Stream != null) {
            Song song = IOFactory.getNewReader(ReaderType.OPENLYRICS).read(xmlStream);
            Song song2 = IOFactory.getNewReader(ReaderType.OPENLYRICS).read(xml2Stream);
            xmlStream.close();
            xml2Stream.close();

            IOFactory.getNewMassWriter(MassWriterType.EASYWORSHIP)
                    .init("result.ews", null)
                    .add(song)
                    .add(song2)
                    .close();
        }
    }
*/
}