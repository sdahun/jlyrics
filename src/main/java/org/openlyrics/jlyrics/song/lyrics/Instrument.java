package org.openlyrics.jlyrics.song.lyrics;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class Instrument implements ILyricsEntry {
    private String name = "i1";
    List<InstrumentLine> lines = new ArrayList<>() {{
        add(new InstrumentLine());
    }};

    @Override
    public ILyricsEntry getDeepCopy() {
        Instrument copy = new Instrument();
        copy.setName(name);

        for (InstrumentLine line : lines) {
            copy.getLines().add(line.getDeepCopy());
        }
        return copy;
    }
}
