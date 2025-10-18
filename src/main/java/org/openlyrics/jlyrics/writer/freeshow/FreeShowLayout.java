package org.openlyrics.jlyrics.writer.freeshow;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FreeShowLayout {
    private String name = "Alapértelmezett";

    private String notes = "";

    private List<FreeShowLayoutItem> slides = new ArrayList<>();

    public void addSlide(FreeShowLayoutItem item) {
        slides.add(item);
    }
}
