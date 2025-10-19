package org.openlyrics.jlyrics.song.lyrics;

import lombok.Data;
import lombok.experimental.Accessors;
import org.openlyrics.jlyrics.song.lyrics.linepart.ILinePart;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public abstract class MixedContainer {
    private List<ILinePart> parts = new ArrayList<>();
    private Boolean multiLine = false;
}
