package com.caniz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleMathTest {

    @Test
    void twoPlusTwoShouldEqualFour() {
        int result = 2 + 2;

        assertEquals(4, result);
    }
}