package org.openlyrics.jlyrics.song.lyrics;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.openlyrics.jlyrics.song.lyrics.linepart.ILinePart;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class InstrumentLine extends MixedContainer implements ILinePart {
    private Integer repeat;
    private Boolean multiLine;

    public InstrumentLine setRepeat(Integer repeat) {
        this.repeat = repeat > 1 ? repeat : null;
        return this;
    }

    public InstrumentLine getDeepCopy() {
        InstrumentLine copy = new InstrumentLine();
        copy.setRepeat(repeat);
        copy.setMultiLine(multiLine);

        for (ILinePart part : getParts()) {
            copy.getParts().add(part.getDeepCopy());
        }

        copy.setMultiLine(getMultiLine());
        return copy;
    }
}