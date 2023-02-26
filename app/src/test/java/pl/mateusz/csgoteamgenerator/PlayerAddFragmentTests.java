package pl.mateusz.csgoteamgenerator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

class PlayerAddFragmentTests {

    @Test
    void checkCorrectNicknameDosiaShouldReturnTrue() {
        String name = "Dosia";
        PlayerAddFragment addFragment = new PlayerAddFragment();
        try {
            Method method = PlayerAddFragment.class.getDeclaredMethod("checkFieldsInformationCorrectness", String.class);
            method.setAccessible(true);
            method.
        } catch (NoSuchMethodException e) {
            fail("Method not found");
        }

    }

}