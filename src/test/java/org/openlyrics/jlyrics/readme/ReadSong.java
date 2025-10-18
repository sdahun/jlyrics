package org.openlyrics.jlyrics.readme;

import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.reader.ReaderType;

import java.io.FileInputStream;

public class ReadSong {
    public static void main(String[] args) {

        String filename = "src/test/resources/songs/original/Amazing Grace.xml";

        try(FileInputStream inputStream = new FileInputStream(filename)){
            Song song = IOFactory.getNewReader(ReaderType.OPENLYRICS).read(inputStream);
            // Do whatever you want with Song object, e.g.:
            System.out.println(song.getProperties().getTitles().get(0).getTitle());
        }
        catch (Exception e) {
            System.out.println("File read error! " + e.getMessage());
        }
    }
}
