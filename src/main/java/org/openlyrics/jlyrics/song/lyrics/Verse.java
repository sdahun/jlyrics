package org.openlyrics.jlyrics.song.lyrics;

import lombok.Data;
import lombok.experimental.Accessors;
import org.openlyrics.jlyrics.util.VerseUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class Verse implements ILyricsEntry {
    private String name = "v1";
    private String lang;
    private String translit;
    List<VerseLine> lines = new ArrayList<>() {{
        add(new VerseLine());
    }};

    public String getFormattedName() {
        if (this.name == null) {
            return null;
        }
        if (this.name.isEmpty()) {
            return "";
        }
        return VerseUtils.getVerseNameByChar(this.name.charAt(0)) + " " + this.name.substring(1);
    }

    public ILyricsEntry getDeepCopy() {
        Verse copy = new Verse();
        copy.setName(name);
        copy.setLang(lang);
        copy.setTranslit(translit);
        copy.getLines().clear();

        for (VerseLine line : lines) {
            copy.getLines().add(line.getDeepCopy());
        }
        return copy;
    }
}
