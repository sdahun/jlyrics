package org.openlyrics.jlyrics.transform;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SongTransformerConfigTest {

    @Test
    void testGetSongbookById() {
        ConfigSongBookData csbd = new ConfigSongBookData("CD", "Cipsum Dorem", "cipsum_dorem");
        SongTransformerConfig stc = new SongTransformerConfig()
            .addSongbookData(new ConfigSongBookData("AB", "Apsum Borem", "apsum_borem"))
            .addSongbookData(csbd)
        ;

        assertEquals(csbd, stc.getSongbookById(1));
    }

    @Test
    void testGetSongbookNames() {
        SongTransformerConfig stc = new SongTransformerConfig()
            .addSongbookData(new ConfigSongBookData("AB", "Apsum Borem", "apsum_borem"))
            .addSongbookData(new ConfigSongBookData("CD", "Cipsum Dorem", "cipsum_dorem"))
        ;

        List<String> expected = new ArrayList<>();
        expected.add("Apsum Borem");
        expected.add("Cipsum Dorem");

        assertEquals(expected, stc.getSongbookNames());
    }
}
