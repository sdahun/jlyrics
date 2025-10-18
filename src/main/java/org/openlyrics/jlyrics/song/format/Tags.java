package org.openlyrics.jlyrics.song.format;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class Tags {
    private String application;
    private List<Tag> entries = new ArrayList<>() {{ add(new Tag()); }};

    public Tags getDeepCopy() {
        Tags copy = new Tags();
        copy.setApplication(application);

        for (Tag tag : entries) {
            copy.getEntries().add(tag.getDeepCopy());
        }
        return copy;
    }
}
