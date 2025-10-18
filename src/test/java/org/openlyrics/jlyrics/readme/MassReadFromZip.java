package org.openlyrics.jlyrics.readme;

import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.massreader.IMassReader;
import org.openlyrics.jlyrics.massreader.MassReaderType;
import org.openlyrics.jlyrics.reader.ReaderType;

public class MassReadFromZip {
    public static void main(String[] args) {

        String filename = "src/test/resources/songs.zip";

        try (IMassReader reader = IOFactory.getNewMassReader(MassReaderType.ZIP)) {
            reader.init(filename, IOFactory.getNewReader(ReaderType.OPENLYRICS));

            while (reader.hasNext()) {
                Song song = reader.next();
                System.out.println(song.getProperties().getTitles().get(0).getTitle());
            }
        }
        catch (Exception e) {
            System.out.println("File read error! " + e.getMessage());
        }
    }
}
