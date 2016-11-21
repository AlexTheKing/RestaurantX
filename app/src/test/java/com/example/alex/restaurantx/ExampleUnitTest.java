package com.example.alex.restaurantx;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void lol() {
        String s = '"' + "abcdefg" + '"';
        System.out.println(s);
        System.out.println(s.substring(1, s.length() - 1));
    }
}