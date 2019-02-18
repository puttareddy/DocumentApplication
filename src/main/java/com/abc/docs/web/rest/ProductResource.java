package com.abc.docs.web.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abc.docs.config.Constants;
import com.abc.docs.security.AuthoritiesConstants;
import com.abc.docs.security.SecurityUtils;
import com.abc.docs.service.MailService;
import com.abc.docs.service.ProductService;
import com.abc.docs.service.dto.ProductDTO;
import com.abc.docs.web.rest.errors.EmailAlreadyUsedException;
import com.abc.docs.web.rest.errors.InternalServerErrorException;
import com.abc.docs.web.rest.util.HeaderUtil;

/**
 * REST controller for managing products.
 */
@RestController
@RequestMapping("/api")
public class ProductResource {

	private final Logger log = LoggerFactory.getLogger(ProductResource.class);

	private final ProductService productService;

	private final MailService mailService;

	public ProductResource(ProductService productService, MailService mailService) {

		this.productService = productService;
		this.mailService = mailService;
	}

	/**
	 * POST /products : Upload the products information using JSON format.
	 *
	 * @param List<ProductDTO>, array of ProductDTO objects
	 * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already
	 *                                   used
	 * @throws RuntimeException          500 (Internal Server Error) if any errors, while processing request
	 */
	@PostMapping("/products")
	public ResponseEntity<?> saveProducts(@Valid @RequestBody List<ProductDTO> productDTOs) {
		String userLogin = SecurityUtils.getCurrentUserLogin()
				.orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
		int count = productService.createProducts(productDTOs);
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Total-Count", Long.toString(count));
		return new ResponseEntity<>(count + " records created", headers, HttpStatus.CREATED);
		// mailService.sendActivationEmail(null);
	}

	/**
     * POST  /upload-products : Upload a Products, which are in CSV file .
     *
     * @param file, all products information
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
     * @throws RuntimeException 500 (Internal Server Error) if any errors, while processing CSV file
     */
    @PostMapping("/upload-products")
    public ResponseEntity<?> uploadProducts(@Valid  @RequestParam("file") MultipartFile file) {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        Map<String, Object> result = new HashMap<String, Object>();
        List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();

        try {
	        final String label = file.getName();
	        final String filepath = "/tmp/" + label;
	        byte[] bytes = file.getBytes();
	        File fh = new File("/tmp/");
	        if(!fh.exists()){
	           fh.mkdir();
	        }

           FileOutputStream writer = new FileOutputStream(filepath);
           writer.write(bytes);
           writer.close();

           log.info("image bytes received: {}", bytes.length);
           
           BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
           String line;
           boolean firstLine = true;
           
           while((line = reader.readLine()) != null) {
              if(firstLine) {
                 firstLine = false;
                 continue;
              }
              String[] part = line.split(",");
              ProductDTO dto = new ProductDTO();
              dto.setCode(Long.valueOf(part[0]));
              dto.setName(part[1]);
              dto.setDescription(part[2]);
              dto.setColor(part[3]);
              dto.setCategory(part[4]);
              dto.setPrice(new BigDecimal(part[5]));
              dto.setImageUrl(part[6]);
              productDTOs.add(dto);
              
           }
           
           result.put("success", true);
           result.put("id", label);
           result.put("error", "");

        }catch(IOException ex) {
           log.error("Failed to process the uploaded image", ex);
           result.put("success", false);
           result.put("id", "");
           result.put("error", ex.getMessage());
           HttpHeaders headers = new HttpHeaders();
           headers.add("X-Total-Count", Long.toString(0));
           return new ResponseEntity<>(result, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
       int count = productService.createProducts(productDTOs);
       HttpHeaders headers = new HttpHeaders();
       headers.add("X-Total-Count", Long.toString(count));
       return new ResponseEntity<>(result, headers, HttpStatus.CREATED);
    }

	/**
	 * GET /users : get all Products.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body all products
	 */
	@GetMapping("/products")
	public ResponseEntity<List<ProductDTO>> getAllUsers() {
		final List<ProductDTO> page = productService.getAllProducts();
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Total-Count", Long.toString(page.size()));
		return new ResponseEntity<>(page, headers, HttpStatus.OK);
	}
	
	 /**
     * DELETE /users/:code : delete a Product based on "code".
     *
     * @param code the code of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/products/{code}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long code) {
    	log.debug("REST request to delete product: {}", code);
        productService.deleteByCode(code);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert( "product.deleted", String.valueOf(code))).build();
	}
    
    /**
     * DELETE /users : delete all Products.
     *
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/products")
	public ResponseEntity<Void> deleteAllProducts() {
    	log.debug("REST request to delete all Products");
    	final List<ProductDTO> page = productService.getAllProducts();
        productService.deleteAll();
        return ResponseEntity.ok().headers(HeaderUtil.createAlert( "products.deleted", String.valueOf(page.size()))).build();
	}
}
