package org.openlyrics.jlyrics.writer.freeshow;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class FreeShowLayoutItem {
    private String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> children;

    public FreeShowLayoutItem(String id) {
        this.id = id;
    }
}
