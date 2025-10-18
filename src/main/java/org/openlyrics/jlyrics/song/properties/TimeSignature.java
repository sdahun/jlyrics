package org.openlyrics.jlyrics.song.properties;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

@Data
@Accessors(chain = true)
public class TimeSignature {
    private static final List<Integer> VALID_DENOMINATORS = List.of(1, 2, 4, 8, 16, 32, 64);

    private int nominator = 4;
    private int denominator = 4;

    public boolean parseString(String signatureString) {
        Pattern pattern = Pattern.compile("(6[0-3]|[1-5][0-9]|[1-9])/(64|32|16|8|4|2|1)");
        Matcher matcher = pattern.matcher(signatureString);
        boolean result = matcher.find();
        if (result) {
            nominator = parseInt(matcher.group(1));
            denominator = parseInt(matcher.group(2));
        }
        return result;
    }

    public boolean isValid() {
        return (nominator > 0) && (nominator < 64) && VALID_DENOMINATORS.contains(denominator);
    }

    public TimeSignature setNominator(int nominator) {
        if (nominator > 0 && nominator < 64) {
            this.nominator = nominator;
        }
        return this;
    }

    public TimeSignature setDenominator(int denominator) {
        if (VALID_DENOMINATORS.contains(denominator)) {
            this.denominator = denominator;
        }
        return this;
    }

    @Override
    public String toString() {
        return nominator + "/" + denominator;
    }

    public TimeSignature getDeepCopy() {
        TimeSignature copy = new TimeSignature();
        copy.setNominator(nominator);
        copy.setDenominator(denominator);
        return copy;
    }
}
