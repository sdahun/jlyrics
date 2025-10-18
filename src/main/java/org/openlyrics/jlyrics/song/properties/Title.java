package org.openlyrics.jlyrics.song.properties;

import lombok.Data;
import lombok.experimental.Accessors;

import static org.openlyrics.jlyrics.song.SongConstants.DEFAULT_TITLE;

@Data
@Accessors(chain = true)
public class Title {
    private String lang;
    private String translit;
    private Boolean original;
    private String title = DEFAULT_TITLE;

    public Title getDeepCopy() {
        Title copy = new Title();
        copy.setLang(lang);
        copy.setTranslit(translit);
        copy.setOriginal(original);
        copy.setTitle(title);
        return copy;
    }
}
