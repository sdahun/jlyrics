package org.openlyrics.jlyrics.song;

import lombok.Data;
import lombok.experimental.Accessors;
import org.openlyrics.jlyrics.song.properties.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class Properties {
    private List<Title> titles = new ArrayList<>();
    private List<Author> authors = new ArrayList<>();
    private String copyright;
    private Integer ccliNo;
    private String released;
    private Integer transposition;
    private Tempo tempo = new Tempo();
    private String key;
    private TimeSignature timeSignature;
    private String variant;
    private String publisher;
    private String version;
    private String keywords;
    private String verseOrder;
    private String collection;
    private Integer trackNo;
    private List<Songbook> songbooks = new ArrayList<>();
    private List<Theme> themes = new ArrayList<>();
    private List<String> comments = new ArrayList<>();

    public Properties() {
        //mandatory element for valid empty song
        titles.add(new Title());
    }

    //ccliNo is positive integer!
    public Properties setCcliNo(Integer ccliNo) {
        if (ccliNo == null) {
            this.ccliNo = null;
        } else {
            this.ccliNo = ccliNo > 0 ? ccliNo : null;
        }
        return this;
    }

    //compat: v0.6, v0.7
    public String getReleaseDate() {
        return this.released;
    }

    public Properties setReleaseDate(String releaseDate) {
        this.released = releaseDate;
        return this;
    }

    //compat: v0.6, v0.7
    public String getCustomVersion() {
        return this.version;
    }

    public Properties setCustomVersion(String customVersion) {
        this.version = customVersion;
        return this;
    }

    //compat: v0.6
    public Properties setTrackNo(Integer trackNo) {
        if (trackNo == null) {
            this.trackNo = null;
        } else {
            this.trackNo = (trackNo > 0) ? trackNo : null;
        }
        return this;
    }

    public Properties setTransposition(Integer transposition) {
        if (transposition == null) {
            this.transposition = null;
        } else {
            this.transposition = (transposition > -100 && transposition < 100) ? transposition : null;
        }
        return this;
    }

    public Properties getDeepCopy() {
        Properties copy = new Properties();
        copy.getTitles().clear();

        for (Title title : titles) {
            copy.getTitles().add(title.getDeepCopy());
        }

        for (Author author : authors) {
            copy.getAuthors().add(author.getDeepCopy());
        }

        copy.setCopyright(copyright);
        copy.setCcliNo(ccliNo);
        copy.setReleased(released);
        copy.setTransposition(transposition);
        copy.setTempo(tempo.getDeepCopy());
        copy.setKey(key);

        if (timeSignature != null) {
            copy.setTimeSignature(timeSignature.getDeepCopy());
        }

        copy.setVariant(variant);
        copy.setPublisher(publisher);
        copy.setVersion(version);
        copy.setKeywords(keywords);
        copy.setVerseOrder(verseOrder);
        copy.setTrackNo(trackNo);

        for (Songbook songbook : songbooks) {
            copy.getSongbooks().add(songbook.getDeepCopy());
        }

        for (Theme theme : themes) {
            copy.getThemes().add(theme.getDeepCopy());
        }

        for (String comment : comments) {
            copy.getComments().add(comment);
        }

        return copy;
    }
}
