package org.openlyrics.jlyrics.writer.freeshow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class FreeShowSlide {

    @JsonIgnore
    private String uid;

    private String group;

    private String color;

    private Map<String, String> settings = new LinkedHashMap<>();

    private String notes = "";

    private List<FreeShowItem> items = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String globalGroup;

    public void setByVerseName(String verseName) {
        switch (verseName.substring(0, 1).toLowerCase()) {
            case "v":
                group = "Versszak";
                color = "#5825f5";
                globalGroup = "verse";
                break;
            case "c":
                group = "Refrén";
                color = "#f525d2";
                globalGroup = "chorus";
                break;
            case "p":
                group = "Elő-refrén";
                color = "#8825f5";
                globalGroup = "pre_chorus";
                break;
            case "b":
                group = "Híd";
                color = "#f52598";
                globalGroup = "bridge";
                break;
            case "i":
                group = "Cím";
                color = "#F012BE";
                break;
            case "e":
                group = "Utójáték";
                color = "#a525f5";
                globalGroup = "outro";
                break;
            default:
                group = "Egyéb";
                color = "#7525f5";
                break;
            //other possible global group values: tag, pre_bridge, break, pre_outro
        }
    }

    public void addItem(FreeShowItem item) {
        items.add(item);
    }
}
