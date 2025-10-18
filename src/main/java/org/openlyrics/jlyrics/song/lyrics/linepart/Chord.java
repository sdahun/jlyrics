package org.openlyrics.jlyrics.song.lyrics.linepart;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.openlyrics.jlyrics.song.lyrics.MixedContainer;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Chord extends MixedContainer implements ILinePart {
    //for backward compatibility (v0.8)
    private String name;

    //entries for v0.9
    private MusicalNote root;
    private MusicalNote bass;
    private String structure;
    private Boolean upbeat;

    public ILinePart getDeepCopy() {
        Chord copy = new Chord();
        copy.setName(name);
        copy.setRoot(root);
        copy.setBass(bass);
        copy.setStructure(structure);
        copy.setUpbeat(upbeat);

        for (ILinePart part : getParts()) {
            copy.getParts().add(part.getDeepCopy());
        }

        copy.setMultiLine(getMultiLine());
        return copy;
    }

}
