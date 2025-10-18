package org.openlyrics.jlyrics.song.lyrics.linepart;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Text implements ILinePart {
    private String content = "";

    public ILinePart getDeepCopy() {
        Text copy = new Text();
        copy.setContent(content);
        return copy;
    }

}
