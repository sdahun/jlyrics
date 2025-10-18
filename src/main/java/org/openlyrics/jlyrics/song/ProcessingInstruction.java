package org.openlyrics.jlyrics.song;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProcessingInstruction {
    private String name;
    private String value;

    public ProcessingInstruction getDeepCopy() {
        ProcessingInstruction copy = new ProcessingInstruction();
        copy.setName(name);
        copy.setValue(value);
        return copy;
    }
}
