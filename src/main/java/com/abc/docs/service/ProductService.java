package com.abc.docs.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abc.docs.domain.Product;
import com.abc.docs.repository.ProductRepository;
import com.abc.docs.service.dto.ProductDTO;
import com.abc.docs.service.mapper.ProductMapper;

/**
 * Service class to manage products.
 */
@Service
@Transactional
public class ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    
    @Autowired
    private ProductMapper mapper;

    public ProductService(ProductRepository userRepository) {
        this.productRepository = userRepository;
    }


    public Product createProduct(ProductDTO productDTO) {
       Product product = mapper.productDTOToProduct(productDTO);
        productRepository.save(product);
        log.debug("Created Information for Product: {}", product);
        return product;
    }

    public int createProducts(List<ProductDTO> productDTOs) {
    	List<Product> products = mapper.productDTOsToProducts(productDTOs);
    	productRepository.saveAll(products);
    	return products.size();
    }
    


    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
    	List<Product> dbList = productRepository.findAll();
        return mapper.productsToProductDTOs(dbList);
    }
    
    public void deleteByCode(Long code) {
    	Product product = new Product();
    	product.setCode(code);
    	productRepository.delete(product);
    }
    
    public void deleteAll() {
    	productRepository.deleteAll();
    }



}
