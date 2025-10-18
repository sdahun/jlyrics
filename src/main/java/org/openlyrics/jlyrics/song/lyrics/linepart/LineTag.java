package org.openlyrics.jlyrics.song.lyrics.linepart;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.openlyrics.jlyrics.song.lyrics.MixedContainer;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class LineTag extends MixedContainer implements ILinePart {
    private String name;
    private Map<String, String> properties = new HashMap<>();

    public ILinePart getDeepCopy() {
        LineTag copy = new LineTag();
        copy.setName(name);

        for (Map.Entry<String, String> property : properties.entrySet()) {
            copy.getProperties().put(property.getKey(), property.getValue());
        }

        for (ILinePart part : getParts()) {
            copy.getParts().add(part.getDeepCopy());
        }

        copy.setMultiLine(getMultiLine());
        return copy;
    }

}
