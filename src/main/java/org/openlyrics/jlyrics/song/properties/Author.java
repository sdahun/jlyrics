package org.openlyrics.jlyrics.song.properties;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Author {
    private AuthorType type;
    private String lang; // only if type is translation
    private String name;

    public Author setLang(String lang) {
        this.lang = (this.type == AuthorType.TRANSLATION) ? lang : null;
        return this;
    }

    public Author getDeepCopy() {
        Author copy = new Author();
        copy.setType(type);
        copy.setLang(lang);
        copy.setName(name);
        return copy;
    }
}
