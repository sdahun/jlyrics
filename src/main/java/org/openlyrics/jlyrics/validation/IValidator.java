package org.openlyrics.jlyrics.validation;

import org.openlyrics.jlyrics.Song;

public interface IValidator {
    boolean isValid(Song song);
}
