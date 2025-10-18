package org.openlyrics.jlyrics.song.lyrics;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.openlyrics.jlyrics.song.lyrics.linepart.ILinePart;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VerseLine extends MixedContainer {
    private String part;
    private String optionalBreak;
    private Integer repeat;

    public VerseLine setOptionalBreak(String optionalBreak) {
        this.optionalBreak = "optional".equals(optionalBreak) ? optionalBreak : null;
        return this;
    }

    public VerseLine setRepeat(Integer repeat) {
        if (repeat == null) {
            this.repeat = null;
        } else {
            this.repeat = repeat >= 2 ? repeat : null;
        }
        return this;
    }

    public VerseLine getDeepCopy() {
        VerseLine copy = new VerseLine();
        copy.setPart(part);
        copy.setOptionalBreak(optionalBreak);
        copy.setRepeat(repeat);

        for (ILinePart part : getParts()) {
            copy.getParts().add(part.getDeepCopy());
        }

        copy.setMultiLine(getMultiLine());
        return copy;
    }
}
