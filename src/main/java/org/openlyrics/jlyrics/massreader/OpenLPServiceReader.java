package org.openlyrics.jlyrics.massreader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.exception.LyricsException;
import org.openlyrics.jlyrics.reader.ILyricsReader;
import org.openlyrics.jlyrics.reader.ReaderType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public class OpenLPServiceReader implements IMassReader {

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ServiceEntry {
        public ServiceItem serviceitem;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ServiceItem {
        public ServiceHeader header;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ServiceHeader {
        public String name;
        public String plugin;
        public String xml_version;
    }

    private List<String> xmlEntries = new ArrayList<>();
    private int currentSong = -1;

    @Override
    public IMassReader init(String path) throws Exception {
        ZipFile zipFile = new ZipFile(path);
        //TODO: earlier versions of OpenLP (>2.0.5) store internal osj file on different filenames!
        InputStream jsonStream = zipFile.getInputStream(zipFile.getEntry("service_data.osj"));

        ObjectMapper mapper = new ObjectMapper();
        List<ServiceEntry> entries = mapper.readValue(jsonStream, new TypeReference<List<ServiceEntry>>(){});
        zipFile.close();
        jsonStream.close();

        this.xmlEntries = entries.stream()
                .filter(e -> e.serviceitem != null &&
                        e.serviceitem.header != null &&
                        e.serviceitem.header.name != null &&
                        e.serviceitem.header.name.equals("songs") &&
                        e.serviceitem.header.plugin.equals("songs"))
                .map(e -> e.serviceitem.header.xml_version)
                .collect(Collectors.toList());

        return this;
    }

    @Override
    public IMassReader init(String path, ILyricsReader reader) throws Exception {
        return this.init(path);
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public boolean hasNext() {
        return (currentSong + 1) < xmlEntries.size();
    }

    @Override
    public Song next() {
        if (this.currentSong >= xmlEntries.size()) {
            return null;
        }

        ++this.currentSong;

        try (
            InputStream xmlStream = new ByteArrayInputStream(this.xmlEntries.get(this.currentSong).getBytes(StandardCharsets.UTF_8))
        ) {
            return IOFactory.getNewReader(ReaderType.OPENLYRICS).read(xmlStream);
        } catch (Exception e) {
            throw new LyricsException(e.getMessage());
        }

    }
}
