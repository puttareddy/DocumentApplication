package com.abc.docs.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.abc.docs.DocumentApplicationApp;
import com.abc.docs.domain.Product;
import com.abc.docs.repository.ProductRepository;
import com.abc.docs.service.dto.ProductDTO;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentApplicationApp.class)
@Transactional
public class ProductServiceIntTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private AuditingHandler auditingHandler;

    @Mock
    DateTimeProvider dateTimeProvider;

    private Product product;

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
         

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now()));
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @Test
    @Transactional
    public void assertThatUserMustExistToResetPassword() {
        productRepository.saveAndFlush(product);
        List<ProductDTO> productsList = productService.getAllProducts();
        assertNotNull(productsList);
        assertEquals(1, productsList.size());

        assertThat(productsList.get(0).getImageUrl()).isEqualTo(product.getImageUrl());
        assertThat(productsList.get(0).getCode()).isEqualTo(product.getCode());
        assertThat(productsList.get(0).getCategory()).isEqualTo(product.getCategory());
    }


}
