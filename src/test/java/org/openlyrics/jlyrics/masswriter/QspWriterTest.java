package org.openlyrics.jlyrics.masswriter;
/*
import org.junit.jupiter.api.Test;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.reader.ILyricsReader;
import org.openlyrics.jlyrics.reader.ReaderType;
import org.openlyrics.jlyrics.transform.SongTransformerConfig;
import org.openlyrics.jlyrics.writer.WriterType;

import java.io.InputStream;
*/
class QspWriterTest {
/*
    @Test
    void testZipWriter() throws Exception {
        try (
            InputStream xml1Stream = this.getClass().getClassLoader().getResourceAsStream("songs/original/Amazing Grace.xml");
            InputStream xml2Stream = this.getClass().getClassLoader().getResourceAsStream("songs/original/Christ Arose.xml");
        ) {
            ILyricsReader reader = IOFactory.getNewReader(ReaderType.OPENLYRICS);
            Song song1 = reader.read(xml1Stream);
            Song song2 = reader.read(xml2Stream);

            SongTransformerConfig config = new SongTransformerConfig()
                .setIntroSongBook(true)
                .setIntroSongNumber(true)
                .setFirstUppercase(true)
            ;

            IOFactory.getNewMassWriter(MassWriterType.ZIP)
                .setFileExtension(".qsp")
                .init("pack", IOFactory.getNewWriter(WriterType.QUELEA), config)
                .add(song1)
                .add(song2)
                .close();
        }
    }
 */
}