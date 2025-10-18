package org.openlyrics.jlyrics.song.lyrics.linepart;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.openlyrics.jlyrics.song.lyrics.MixedContainer;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Beat extends MixedContainer implements ILinePart {

    public ILinePart getDeepCopy() {
        Beat copy = new Beat();

        for (ILinePart part : getParts()) {
            copy.getParts().add(part.getDeepCopy());
        }

        copy.setMultiLine(getMultiLine());
        return copy;
    }
}
