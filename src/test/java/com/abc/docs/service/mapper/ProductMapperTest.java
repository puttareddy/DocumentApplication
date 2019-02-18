package com.abc.docs.service.mapper;


import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.abc.docs.DocumentApplicationApp;
import com.abc.docs.domain.Product;
import com.abc.docs.service.dto.ProductDTO;

/**
 * Test class for the UserMapper.
 *
 * @see UserMapper
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentApplicationApp.class)
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    private Product product;
    private ProductDTO productDTO;

    @Before
    public void init() {
       product = new Product();
       product.setCode(Long.valueOf(1));
       product.setCategory("laptop");
       product.setColor("black");
       product.setName("DELL xperia");
       product.setDescription("DELL intel core I5");
       product.setRate(new BigDecimal("500"));
       product.setImageUrl("/assets/dell.png");
       productDTO = new ProductDTO(product);
    }

    @Test
    public void productToProductDTOsShouldMapOnlyNonNullUsers(){
        List<Product> products = new ArrayList<>();
        products.add(product);
        products.add(null);

        List<ProductDTO> productDTOS = productMapper.productsToProductDTOs(products);

        assertThat(productDTOS).isNotEmpty();
        assertThat(productDTOS).size().isEqualTo(1);
    }

    @Test
    public void userDTOsToUsersShouldMapOnlyNonNullUsers(){
        List<ProductDTO> productsDto = new ArrayList<>();
        productsDto.add(productDTO);
        productsDto.add(null);

        List<Product> products = productMapper.productDTOsToProducts(productsDto);

        assertThat(products).isNotEmpty();
        assertThat(products).size().isEqualTo(1);
    }

    @Test
    public void productDTOtoProductMapWithNullUserShouldReturnNull(){
        assertThat(productMapper.productDTOToProduct(null)).isNull();
    }

}
