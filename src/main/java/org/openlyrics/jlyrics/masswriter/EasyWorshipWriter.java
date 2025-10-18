package org.openlyrics.jlyrics.masswriter;

import lombok.Getter;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.transform.SongTransformerConfig;
import org.openlyrics.jlyrics.writer.ILyricsWriter;
import org.openlyrics.jlyrics.writer.WriterType;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;

public class EasyWorshipWriter implements IMassWriter {

    @Getter
    static class EasyWorshipEntry {
        private String title;
        private String author;
        private String copyright;
        private int originalContentSize;
        private int zippedContentSize;
        private byte[] zippedContent;

        public EasyWorshipEntry setTitle(String title) {
            this.title = (title.length() > 50) ? title.substring(0,50) : title;
            return this;
        }

        public EasyWorshipEntry setAuthor(String author) {
            this.author = (author == null) ? null : ((author.length() > 50) ? author.substring(0,50) : author);
            return this;
        }

        public EasyWorshipEntry setCopyright(String copyright) {
            this.copyright = (copyright == null) ? null : ((copyright.length() > 50) ? copyright.substring(0,50) : copyright);
            return this;
        }

        public EasyWorshipEntry setContent(String content) {
            this.originalContentSize = content.length();

            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
            deflater.setInput(content.getBytes());
            deflater.finish();
            byte[] output = new byte[this.originalContentSize];
            this.zippedContentSize = deflater.deflate(output);
            this.zippedContent = Arrays.copyOf(output, this.zippedContentSize);

            return this;
        }
    }


    private String path;
    private List<EasyWorshipEntry> entries;
    private SongTransformerConfig config;
    private int batchCounter;

    @Override
    public String getFileExtension() {
        return ".ews";
    }

    @Override
    public IMassWriter init(String path, ILyricsWriter writer, SongTransformerConfig config) throws Exception {
        this.path = path;
        this.entries = new ArrayList<>();
        this.config = config;
        this.batchCounter = 0;
        return this;
    }

    @Override
    public IMassWriter add(Song song) throws Exception {
        //check batch amount
        if (config.getBatchSize() > 0 && entries.size() >= config.getBatchSize()) {
            close();
            ++batchCounter;
            this.entries.clear();
        }

        ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
        IOFactory.getNewWriter(WriterType.RTF).write(song, contentStream);

        this.entries.add(
                new EasyWorshipEntry()
                        .setTitle(song.getProperties().getTitles().get(0).getTitle())
                        .setAuthor(!song.getProperties().getAuthors().isEmpty() ? song.getProperties().getAuthors().get(0).getName() : null)
                        .setCopyright(song.getProperties().getCopyright() != null ? song.getProperties().getCopyright() : null)
                        .setContent(contentStream.toString())
        );

        return this;
    }

    @Override
    public void close() throws Exception {
        int headerSize = 0x3e;
        short entrySize = 0x718;

        String batchSuffix = batchCounter > 0 ? "-" + batchCounter : "";

        int outputFileSize =
                headerSize +
                this.entries.size() * entrySize +
                this.entries.stream().mapToInt(c -> c.getZippedContentSize() + 0x0e).sum();

        ByteBuffer output = ByteBuffer.allocate(outputFileSize);
        output.order(ByteOrder.LITTLE_ENDIAN);
        output
            .put("EasyWorship Schedule File Version    5".getBytes())
            .putInt(0x00001a00)
            .putInt(0)
            .putInt(0x00004014)
            .putInt(0)
            .putShort((short) 0x4014)
            .putInt(this.entries.size())
            .putShort(entrySize);

        int compressedPos = headerSize + this.entries.size() * entrySize;

        for (int i = 0; i < this.entries.size(); i++) {
            EasyWorshipEntry entry = this.entries.get(i);
            int basePos = headerSize + i * entrySize;

            //title
            output.position(basePos);
            output.put(entry.getTitle().getBytes("Windows-1250"));

            //author
            if (entry.getAuthor() != null) {
                output.position(basePos + 307);
                output.put(entry.getAuthor().getBytes("Windows-1250"));
            }

            //copyright
            if (entry.getCopyright() != null) {
                output.position(basePos + 358);
                output.put(entry.getCopyright().getBytes("Windows-1250"));
            }

            //linked, default background
            output.position(basePos + 510);
            output.putShort((short) 0x0101);

            //start of compressed content
            output.position( basePos + 800);
            output.putInt(compressedPos);

            //compressed content
            output.position(compressedPos);
            output
                .putInt(entry.getZippedContentSize() + 10)
                .put(entry.getZippedContent())
                .putInt(0x04034b51)
                .putInt(entry.getOriginalContentSize())
                .putShort((short) 8);

            //next compressed content start position
            compressedPos += entry.getZippedContentSize() + 14;


            //type: song
            output.position(basePos + 820);
            output.putInt(1);
        }

        FileOutputStream outputStream = new FileOutputStream(this.path + batchSuffix + getFileExtension());
        outputStream.write(output.array());
        outputStream.close();
    }
}
