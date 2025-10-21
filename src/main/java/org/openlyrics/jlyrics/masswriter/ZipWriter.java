package org.openlyrics.jlyrics.masswriter;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.transform.SongTransformerConfig;
import org.openlyrics.jlyrics.writer.ILyricsWriter;

import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipWriter implements IMassWriter {
    String fileExtension = ".zip";
    String path;
    ILyricsWriter writer;
    SongTransformerConfig config;
    ZipOutputStream outputStream;
    int itemCounter;
    int batchCounter;

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public IMassWriter setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
        return this;
    }

    @Override
    public IMassWriter init(String path, ILyricsWriter writer, SongTransformerConfig config) throws Exception {
        this.path = path;
        this.writer = writer;
        this.config = config;

        this.outputStream = new ZipOutputStream(new FileOutputStream(path + getFileExtension()));
        this.outputStream.setLevel(9);
        this.itemCounter = 0;
        this.batchCounter = 0;
        return this;
    }

    @Override
    public IMassWriter add(Song song) throws Exception {
        //check batch boundary
        if (config.getBatchSize() > 0 && itemCounter >= config.getBatchSize()) {
            ++batchCounter;
            this.outputStream.close();
            this.outputStream = new ZipOutputStream(new FileOutputStream(path + "-" + batchCounter + getFileExtension() ));
            itemCounter = 0;
        }

        ZipEntry entry = new ZipEntry(this.writer.getFullFilename(song));
        this.outputStream.putNextEntry(entry);
        this.writer.write(song, this.outputStream);
        this.outputStream.closeEntry();
        ++itemCounter;
        return this;
    }

    @Override
    public void close() throws Exception {
        this.outputStream.close();
    }
}
