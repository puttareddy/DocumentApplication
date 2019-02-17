package com.abc.docs.web.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abc.docs.security.SecurityUtils;
import com.abc.docs.service.MailService;
import com.abc.docs.service.ProductService;
import com.abc.docs.service.dto.ProductDTO;
import com.abc.docs.web.rest.errors.EmailAlreadyUsedException;
import com.abc.docs.web.rest.errors.InternalServerErrorException;

/**
 * REST controller for managing the current user's account.
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
	 * POST /account : update the current user information.
	 *
	 * @param userDTO the current user information
	 * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already
	 *                                   used
	 * @throws RuntimeException          500 (Internal Server Error) if the user
	 *                                   login wasn't found
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
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already used
     * @throws RuntimeException 500 (Internal Server Error) if the user login wasn't found
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
              dto.setPrice(Float.valueOf(part[5]));
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
	 * GET /users : get all users.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all users
	 */
	@GetMapping("/products")
	public ResponseEntity<List<ProductDTO>> getAllUsers(Pageable pageable) {
		final List<ProductDTO> page = productService.getAllProducts();
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Total-Count", Long.toString(page.size()));
		return new ResponseEntity<>(page, headers, HttpStatus.OK);
	}
}
