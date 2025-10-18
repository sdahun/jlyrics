package org.openlyrics.jlyrics.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.writer.freeshow.FreeShowSong;

import java.io.OutputStream;

public class FreeShowWriter implements ILyricsWriter {

    //Filename without song number because FreeShow uses filename as title
    @Override
    public String getFileName(Song song) {
        return song.getProperties().getTitles().get(0).getTitle();
    }

    @Override
    public String getFileExtension() {
        return ".show";
    }

    @Override
    public void write(Song song, OutputStream outputStream) throws Exception {
        FreeShowSong fss = new FreeShowSong(song);

        Object[] simpleSong = new Object[2];
        simpleSong[0] = fss.getUid();
        simpleSong[1] = fss;

        ObjectMapper mapper = new ObjectMapper();
        JsonGenerator generator = mapper.getFactory().createGenerator(outputStream);
        generator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        mapper.writeValue(generator, simpleSong);
    }
}
