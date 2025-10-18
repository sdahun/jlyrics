package org.openlyrics.jlyrics.writer.freeshow;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FreeShowLine {
    private String align = "";

    private List<FreeShowLineText> text = new ArrayList<>();

    public void addText(String value) {
        FreeShowLineText textElement = new FreeShowLineText();
        textElement.setValue(value);
        text.add(textElement);
    }
}
