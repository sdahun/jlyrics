package org.openlyrics.jlyrics.writer;

import org.openlyrics.jlyrics.Song;
import org.openlyrics.jlyrics.util.SongUtils;

import java.io.OutputStream;

public class RtfWriter implements ILyricsWriter {

    @Override
    public String getFileExtension() {
        return ".rtf";
    }

    @Override
    public void write(Song song, OutputStream outputStream) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
            .append("{\\rtf1\\ansi\\deff0\\deftab254{\\fonttbl{\\f0\\fnil\\fcharset238 Arial;}}")
            .append("{\\colortbl\\red0\\green0\\blue0;\\red255\\green0\\blue0;\\red0\\green128\\blue0;")
            .append("\\red0\\green0\\blue255;\\red255\\green255\\blue0;\\red255\\green0\\blue255;")
            .append("\\red128\\green0\\blue128;\\red128\\green0\\blue0;\\red0\\green255\\blue0;")
            .append("\\red0\\green255\\blue255;\\red0\\green128\\blue128;\\red0\\green0\\blue128;")
            .append("\\red255\\green255\\blue255;\\red192\\green192\\blue192;\\red128\\green128\\blue128;")
            .append("\\red255\\green255\\blue255;}\\paperw12240\\paperh15840")
            .append("\\margl1880\\margr1880\\margt1440\\margb1440")
            .append("{\\*\\pnseclvl1\\pnucrm\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n")
            .append("{\\*\\pnseclvl2\\pnucltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n")
            .append("{\\*\\pnseclvl3\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{.}}}\r\n")
            .append("{\\*\\pnseclvl4\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb}{\\pntxta{)}}}\r\n")
            .append("{\\*\\pnseclvl5\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n")
            .append("{\\*\\pnseclvl6\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n")
            .append("{\\*\\pnseclvl7\\pnlcrm\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n")
            .append("{\\*\\pnseclvl8\\pnlcltr\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n")
            .append("{\\*\\pnseclvl9\\pndec\\pnstart1\\pnhang\\pnindent720{\\pntxtb{(}}{\\pntxta{)}}}\r\n");

        String[] replaceFrom = {
            "á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű",
            "Á", "É", "Í", "Ó", "Ö", "Ő", "Ú", "Ü", "Ű",
            "„", "”", "’", "\\{", "}"
        };

        String[] replaceTo = {
            "\\\\'e1", "\\\\'e9", "\\\\'ed", "\\\\'f3", "\\\\'f6", "\\\\'f5", "\\\\'fa", "\\\\'fc", "\\\\'fb",
            "\\\\'c1", "\\\\'c9", "\\\\'cd", "\\\\'d3", "\\\\'d6", "\\\\'d5", "\\\\'da", "\\\\'dc", "\\\\'db",
            "\\\\'84", "\\\\'94", "\\\\'92", "\\\\{", "\\\\}"
        };

        String text = SongUtils.getSongTextContent(song);

        for (int i = 0; i < replaceFrom.length; i++) {
            text = text.replaceAll(replaceFrom[i], replaceTo[i]);
        }

        boolean firstLine = true;
        for (String line : text.split("\r\n")) {
            line = line.replaceAll("\n", "\\\\line ");
            if (firstLine) {
                stringBuilder
                    .append("{\\pard\\ql\\li0\\fi0\\ri0\\sb0\\sl\\sa0 \\plain\\f0\\fs20\\fntnamaut ")
                    .append(line);
                firstLine = false;
            }
            else {
                stringBuilder
                    .append("\\par\r\n\\ql\\li0\\fi0\\ri0\\sb0\\sl\\sa0 \\plain\\f0\\fs20\\fntnamaut ")
                    .append(line);
            }
        }
        stringBuilder.append("}\r\n}");

        outputStream.write(stringBuilder.toString().getBytes());
    }
}
