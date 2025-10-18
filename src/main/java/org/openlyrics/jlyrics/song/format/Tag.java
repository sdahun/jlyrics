package org.openlyrics.jlyrics.song.format;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Tag {
    private String name = "t1";
    private String open = "<span>";
    private String close;

    public Tag getDeepCopy() {
        Tag copy = new Tag();
        copy.setName(name);
        copy.setOpen(open);
        copy.setClose(close);
        return copy;
    }
}
