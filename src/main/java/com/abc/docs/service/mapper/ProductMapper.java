package com.abc.docs.service.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.abc.docs.domain.Category;
import com.abc.docs.domain.Product;
import com.abc.docs.service.dto.ProductDTO;

/**
 * Mapper for the entity Product and its DTO called ProductDTO.
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
public class ProductMapper {

    public List<ProductDTO> ProductsToProductDTOs(List<Product> products) {
        return products.stream()
            .filter(Objects::nonNull)
            .map(this::productToProductDTO)
            .collect(Collectors.toList());
    }

    public ProductDTO productToProductDTO(Product product) {
        return new ProductDTO(product);
    }

    public List<Product> ProductDTOsToProducts(List<ProductDTO> ProductDTOs) {
        return ProductDTOs.stream()
            .filter(Objects::nonNull)
            .map(this::ProductDTOToProduct)
            .collect(Collectors.toList());
    }

    public Product ProductDTOToProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        } else {
            Product product = new Product();
            product.setCode(productDTO.getCode());
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setRate(productDTO.getPrice());
            product.setColor(productDTO.getColor());
            product.setCategory(productDTO.getCategory());
            product.setImageUrl(productDTO.getImageUrl());
            return product;
        }
    }


    public Product ProductFromId(Long id) {
        if (id == null) {
            return null;
        }
        Product Product = new Product();
        Product.setCode(id);
        return Product;
    }
}
