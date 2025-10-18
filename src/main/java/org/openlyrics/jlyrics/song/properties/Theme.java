package org.openlyrics.jlyrics.song.properties;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Theme {
    private String theme;
    private Integer ccliThemeId;
    private String lang;
    private String translit;

    //until v0.7
    public Theme setCcliThemeId(Integer ccliThemeId) {
        if (ccliThemeId == null) {
            this.ccliThemeId = null;
        } else {
            this.ccliThemeId = (ccliThemeId > 0 && ccliThemeId < 1000) ? ccliThemeId : null;
        }
        return this;
    }

    public Theme getDeepCopy() {
        Theme copy = new Theme();
        copy.setTheme(theme);
        copy.setCcliThemeId(ccliThemeId);
        copy.setLang(lang);
        copy.setTranslit(translit);
        return copy;
    }
}
