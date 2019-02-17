package com.abc.docs.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abc.docs.domain.Product;
import com.abc.docs.repository.AuthorityRepository;
import com.abc.docs.repository.ProductRepository;
import com.abc.docs.service.dto.ProductDTO;
import com.abc.docs.service.mapper.ProductMapper;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    
    @Autowired
    private ProductMapper mapper;

    public ProductService(ProductRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, CacheManager cacheManager) {
        this.productRepository = userRepository;
    }


    public Product createProduct(ProductDTO productDTO) {
       Product product = mapper.ProductDTOToProduct(productDTO);
        productRepository.save(product);
        log.debug("Created Information for Product: {}", product);
        return product;
    }

    public int createProducts(List<ProductDTO> productDTOs) {
    	List<Product> products = mapper.ProductDTOsToProducts(productDTOs);
    	productRepository.saveAll(products);
    	return products.size();
    }
    


    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
    	List<Product> dbList = productRepository.findAll();
        return mapper.ProductsToProductDTOs(dbList);
    }



}
