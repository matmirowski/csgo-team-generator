package pl.mateusz.csgoteamgenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.*;
import java.io.IOException;

class DataHandlerTest {

    @Test
    void getPlayerImageUrl_Snax_ShouldReturnCorrectUrl() {
        String name = "Snax";
        String expected = "https://liquipedia.net/commons/images/1/1a/Snax_Moche_XL_Esports_2019.jpg";
        String actual = DataHandler.getPlayerImageUrl(name);
        assertEquals(expected, actual);
    }

    @Test
    void getPlayerImageUrl_HadesPolishPlayer_ShouldReturnCorrectUrl() {
        String name = "Hades_(Polish_player)";
        String expected = "https://liquipedia.net/commons/images/7/76/Hades_at_Antwerp_Major_2022_EU_RMR.jpg";
        String actual = DataHandler.getPlayerImageUrl(name);
        assertEquals(expected, actual);
    }

    @Test
    void getPlayerImageUrl_NonExistingPlayer_ShouldReturnNull() {
        String name = "lol";
        String expected = null;
        String actual = DataHandler.getPlayerImageUrl(name);
        assertEquals(expected, actual);
    }

    @Test
    void getSiteHtmlHead_Snax_ShouldReturnCorrectHead() {
        String playerProfileUrl = "https://liquipedia.net/counterstrike/Snax";
        String userAgentHead = "Safari/5.0 (Macintosh; Intel Mac OS X 10_11_1) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/46.0.2454.101 Safari/321.0";
        try {
            String expected = Jsoup
                    .connect(playerProfileUrl)
                    .timeout(1000)
                    .userAgent(userAgentHead)
                    .get()
                    .head()
                    .getElementsByTag("meta")
                    .get(10)
                    .toString();
            Element actual = DataHandler.getSiteHtmlHead(playerProfileUrl);
            assertEquals(expected, actual.getElementsByTag("meta").get(10).toString());
        } catch (IOException e) {
            fail("IOException thrown");
        }
    }

    @Test
    void getSiteHtmlHead_NonExistingPlayer_ShouldReturnNull() {
        String playerProfileUrl = "https://liquipedia.net/counterstrike/Lol";
        Element expected = null;
        Element actual = DataHandler.getSiteHtmlHead(playerProfileUrl);
        assertEquals(expected, actual);
    }

    @Test
    void removeSuffixFromPlayerName_NameWithNoSuffix_ShouldReturnName() {
        String name = "Snax";
        String expected = "Snax";
        String actual = DataHandler.removeSuffixFromPlayerName(name);
        assertEquals(expected, actual);
    }

    @Test
    void removeSuffixFromPlayerName_NameWithPlayerSuffix_ShouldReturnFormattedName() {
        String name = "Hades_(Polish_player)";
        String expected = "Hades";
        String actual = DataHandler.removeSuffixFromPlayerName(name);
        assertEquals(expected, actual);
    }
}