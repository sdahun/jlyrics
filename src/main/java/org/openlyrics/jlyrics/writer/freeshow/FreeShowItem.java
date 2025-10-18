package org.openlyrics.jlyrics.writer.freeshow;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FreeShowItem {
    private String type = "text";

    private List<FreeShowLine> lines = new ArrayList<>();

    private String style = "top:120px;left:50px;height:840px;width:1820px;";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String align;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean auto;

    public void addLine(FreeShowLine line) {
        lines.add(line);
    }
}
