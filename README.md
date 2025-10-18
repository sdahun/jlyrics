# JLyrics - Java library to handle song lyrics

JLyrics is a free (MIT licensed) java library to handle lyrics in all version of
[OpenLyrics](https://docs.openlyrics.org/en/latest/)
([v0.6, v0.7, v0.8, v0.9](https://github.com/openlyrics/openlyrics/)) format.
Besides this main (.xml) format, this library can
- read and write [OpenLP](https://openlp.org) song database (songs.sqlite) file (in version 8 format)
- read and write zip file which contains OpenLyrics xml files
- read [OpenLP](https://openlp.org) service (.osz) file 
- write [FreeShow](https://freeshow.app) song (.show) file
- write [EasyWorship](https://www.easyworship.com) Schedule (.ews) file (in version 5 format)
- write lyrics to PowerPoint presentation (.pptx) file.
- write lyrics to plain text (.txt) and rich text (.rtf) file.

The library contains some utilities, which helps transform, extract and validate the consistency of lyrics.

## 1. Single song input/output

Every reader reads from InputStream and every writer writes to OutputStream.
The possible readers can be found in ReaderType enum class, the writers can be found in
WriterType enum.

### 1.1. Read from OpenLyrics XML file

```java
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.reader.ReaderType;

import java.io.FileInputStream;

public class ReadSong {
    static void main(String[] args) {

        String filename = "src/test/resources/songs/original/Amazing Grace.xml";

        try(
                FileInputStream inputStream = new FileInputStream(filename)
        ){
            Song song = IOFactory.getNewReader(ReaderType.OPENLYRICS).read(inputStream);
            // Do whatever you want with Song object, e.g.:
            System.out.println(song.getProperties().getTitles().get(0).getTitle());
        }
        catch (Exception e) {
            System.out.println("File read error! " + e.getMessage());
        }
    }
}
```

### 1.2. Write to OpenLyrics XML file

```java
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.writer.WriterType;

import java.io.FileOutputStream;

public class WriteSong {
    static void main(String[] args) {

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
```

## 2. Mass song input/output

### 2.1. Read multiple songs

These are the MassReaderTypes:
- MassReader.ZIP - for read from zip file with the initialized ReaderType format (xml)
- MassReader.OPENLP_DB - for read from OpenLP songs.sqlite database v8 version
- MassReader.OPENLP_SERVICE - for read from OpenLP service.osz file

Sample code to read xml files from zip:

```java
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.massreader.IMassReader;
import org.openlyrics.jlyrics.massreader.MassReaderType;
import org.openlyrics.jlyrics.reader.ReaderType;

public class MassReadFromZip {
    static void main(String[] args) {

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
```

### 2.2. Write multiple songs

These are the valid MassWriterTypes:
- MassWriterType.OPENLP - for write to OpenLP songs.sqlite database v8 format
- MassWriterType.ZIP - for write to zip file with the initialized WriterType format (xml, text, rtf, pptx)
- MassWriterType.EASYWORSHIP - for write to EasyWorship Schedule 5 format

At writer initialization the second argument can be null if the inner song format is determined.

Sample code to write xml files into a zip file:

```java
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.IOFactory;
import org.openlyrics.jlyrics.masswriter.IMassWriter;
import org.openlyrics.jlyrics.masswriter.MassWriterType;
import org.openlyrics.jlyrics.transform.SongTransformerConfig;
import org.openlyrics.jlyrics.writer.WriterType;

public class MassWriteToZip {
    static void main(String[] args) {
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
```

## 3. Create song object programmatically

When instantiate a new Song() object, it contains all required element to generate valid xml on serialization.
Every setter have chain accessor, so it's possible to set or alter any property in one line:

```java
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.Properties;
import org.openlyrics.jlyrics.song.SongVersion;
import org.openlyrics.jlyrics.song.lyrics.Verse;
import org.openlyrics.jlyrics.song.lyrics.VerseLine;
import org.openlyrics.jlyrics.song.lyrics.linepart.LineTag;
import org.openlyrics.jlyrics.song.lyrics.linepart.Text;
import org.openlyrics.jlyrics.song.properties.Author;
import org.openlyrics.jlyrics.song.properties.Theme;
import org.openlyrics.jlyrics.song.properties.Title;
import org.openlyrics.jlyrics.util.SongUtils;
import org.openlyrics.jlyrics.writer.OpenLyricsWriter;
import org.openlyrics.jlyrics.writer.XmlWriter;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class CreateSong {
    static void main(String[] args) {
        Song song = new Song()
                .setVersion(SongVersion.V0_8)
                .setCreatedIn("SmartLyrics 1.0")
                .setModifiedIn("SmartLyrics 1.0")
                .setModifiedDate(SongUtils.dateToString(ZonedDateTime.now()))
                .setProperties(new Properties()
                        .setTitles(new ArrayList<>() {{
                            add(new Title().setTitle("Amazing Grace"));
                            add(new Title()
                                    .setTitle("Az Úr irgalma végtelen")
                                    .setLang("hu")
                            );
                        }})
                        .setAuthors(new ArrayList<>() {{
                            add(new Author().setName("John Newton"));
                        }})
                        .setCopyright("1982 Jubilate Hymns Limited")
                        .setCcliNo(1037882)
                        .setThemes(new ArrayList<>() {{
                            add(new Theme().setTheme("God's Attributes"));
                        }})
                )
                .setLyrics(new ArrayList<>() {{
                    add(new Verse().setLines(new ArrayList<>() {{
                        add((VerseLine) new VerseLine().setParts(new ArrayList<>() {{
                            add(new Text().setContent("Amazing grace! How sweet the sound!"));
                            add(new LineTag().setName("br"));
                            add(new Text().setContent("That saved a wretch like me!"));
                            add(new LineTag().setName("br"));
                            add(new Text().setContent("I once was lost, but now I'm found,"));
                            add(new LineTag().setName("br"));
                            add(new Text().setContent("Was blind, but now I see."));
                        }}));
                    }}));
                }});

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            OpenLyricsWriter writer = new OpenLyricsWriter();
            writer.write(song, baos);
            System.out.println(baos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

## 4. Song transformation

This library can alter lyrics according to some configuration settings. These settings can
be found in SongTransformerConfig class:

- _**introSlide**_: If this value is true, then the SongTransformer will generate a new
  slide before the first one and puts the primary title of the song on it.

- _**introSongBook**_: If this value is true, then the introSlide will be supplemented with
  the primary songbook name also.

- _**introSongNumber**_: If this value is true, then the introSlide will be supplemented with
  the song number from the primary songbook also.

- **_lineBreak_**: If this value is false, then all line breaks will be removed from lyrics
  and the text will be continuous.

- **_solidusSeparator_**: If this value is true and lineBreak is false, then all line breaks
  will be replaced by / signs.

- **_firstUppercase_**: If this value is true and lineBreak is true, then it will capitalize
  all words at the beginning of every new line.

- **_repeatVerses_**: If this value is true and the verseOrder contains repeated slides,
  then these slides will be repeated in the final output, which is useful for example in pptx
  output.

- emptySlide: If this value is true, then the song will be appended an empty slide as the
  last slide, which is useful for example in pptx output.

- tagSlide: If this value is true, then the song will be appended with a slide which
  contains a speedsearch abbreviation which contains the abbreviation of the primary songbook
  and the song number from the primary songbook. For this function the possible
  abbreviations should be setted through ConfigSongBookData classes.

Example for song transformation:

```java
import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.song.properties.Songbook;
import org.openlyrics.jlyrics.transform.ConfigSongBookData;
import org.openlyrics.jlyrics.transform.SongTransformer;
import org.openlyrics.jlyrics.transform.SongTransformerConfig;
import org.openlyrics.jlyrics.writer.OpenLyricsWriter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class TransformSong {
    static void main(String[] args) {

        Song song = new Song();
        song.getProperties()
                .setSongbooks(new ArrayList<>() {{
                    add(new Songbook().setName("Hymns and Praises").setEntry("123"));
                }})
                .setVerseOrder("v1");

        SongTransformerConfig config = new SongTransformerConfig()
                .setIntroSlide(true)
                .setIntroSongBook(true)
                .setIntroSongNumber(true)

                .setLineBreak(true)
                .setFirstUppercase(true)
                .setRepeatVerses(false)

                .setEmptySlide(true)
                .setTagSlide(true)

                .addSongbookData(new ConfigSongBookData("HP", "Hymns and Praises", "hymns_and_praises"));

        song = new SongTransformer().transform(song, config);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            OpenLyricsWriter writer = new OpenLyricsWriter();
            writer.write(song, baos);
            System.out.println(baos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```
