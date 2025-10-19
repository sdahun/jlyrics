package org.openlyrics.jlyrics.song.properties;

import lombok.Data;
import lombok.experimental.Accessors;

import static java.lang.Integer.parseInt;

@Data
@Accessors(chain = true)
public class Tempo {
    private TempoType type;
    private String value;

    public Tempo setValue(String value) {
        if (this.type == null) {
            this.value = null;
        }
        else {
            try {
                if (this.type == TempoType.BPM) {
                    this.value = (parseInt(value) >= 30 && parseInt(value) <= 250) ? value : null;
                } else if (this.type == TempoType.TEXT) {
                    this.value = value;
                }
            }
            catch(NumberFormatException e) {
                this.value = null;
            }
        }
        return this;
    }

    public boolean isEmpty() {
        return (type == null) && (value == null);
    }

    public void clear() {
        this.type = null;
        this.value = null;
    }

    public Tempo getDeepCopy() {
        Tempo copy = new Tempo();
        copy.setType(type);
        copy.setValue(value);
        return copy;
    }
}
