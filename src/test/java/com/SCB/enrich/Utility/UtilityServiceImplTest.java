package com.SCB.enrich.Utility;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class UtilityServiceImplTest {

    @Autowired
    UtilityServiceImpl underTest;

    @Test
    void isInteger() {
        // given
        // when
        boolean expectedTrue = underTest.isInteger("5");
        boolean expectedFalse = underTest.isInteger("W");

        // then
        assertThat(expectedTrue).isTrue();
        assertThat(expectedFalse).isFalse();
    }

    @Test
    void isDouble() {
        // given
        // when
        boolean expectedTrue_1 = underTest.isDouble("15");
        boolean expectedTrue_2 = underTest.isDouble("15.5");
        boolean expectedFalse = underTest.isDouble("W");

        // then
        assertThat(expectedTrue_1).isTrue();
        assertThat(expectedTrue_2).isTrue();
        assertThat(expectedFalse).isFalse();
    }
}