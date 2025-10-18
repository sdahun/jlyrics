package org.openlyrics.jlyrics.readme;

import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.writer.WriterType;

import java.io.FileOutputStream;

public class WriteSong {
    public static void main(String[] args) {

        try (FileOutputStream outputStream = new FileOutputStream("song.xml")) {
            Song song = new Song();
            song.getProperties().getTitles().get(0).setTitle("Sample song");

            IOFactory.getNewWriter(WriterType.OPENLYRICS).write(song, outputStream);
        }
        catch (Exception e) {
            System.out.println("File write error! " + e.getMessage());
        }
    }
}
