package com.techchallenge.domain.entity;

import com.techchallenge.domain.enums.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {


    @Test
    void testProductWithSucess() {
        Product product = new Product( "sku9876","X Bacon", "LANCHE", "test",
                new BigDecimal("10.0"), "image");
        assertEquals("X Bacon",product.getTitle(), "Must Be Equals");
        assertEquals("test",product.getDescription(), "Must Be Equals");
        assertEquals("image",product.getImage(), "Must Be Equals");
        assertEquals(new BigDecimal("10.0"),product.getPrice(), "Must Be Equals");
        assertEquals(Category.LANCHE,product.getCategory(), "Must Be Equals");
    }

    @Test
    void testProductWithInvalidCategory() {
        Assertions.assertThatThrownBy(
                () -> new Product("sku12098","X Bacon", "xx", "test", new BigDecimal("10.0"), ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Category!");
    }


}