package org.openlyrics.jlyrics.song.properties;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Songbook {
    private String name;
    private String entry;

    public Songbook getDeepCopy() {
        Songbook copy = new Songbook();
        copy.setName(name);
        copy.setEntry(entry);
        return copy;
    }
}
