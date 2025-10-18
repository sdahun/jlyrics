package org.openlyrics.jlyrics.readme;

import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.masswriter.IMassWriter;
import org.openlyrics.jlyrics.masswriter.MassWriterType;
import org.openlyrics.jlyrics.transform.SongTransformerConfig;
import org.openlyrics.jlyrics.writer.WriterType;

public class MassWriteToZip {
    public static void main(String[] args) {
        Song song1 = new Song();
        song1.getProperties().getTitles().get(0).setTitle("Amazing Grace");

        Song song2 = new Song();
        song2.getProperties().getTitles().get(0).setTitle("Are You Washed");

        SongTransformerConfig config = new SongTransformerConfig();

        try (IMassWriter writer = IOFactory.getNewMassWriter(MassWriterType.ZIP)) {
            writer
                .init("songs", IOFactory.getNewWriter(WriterType.OPENLYRICS), config)
                .add(song1)
                .add(song2)
                .close();
        } catch (Exception e) {
            System.out.println("File write error! " + e.getMessage());
        }
    }
}
