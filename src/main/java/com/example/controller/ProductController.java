package com.example.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.pojo.Product;
import com.example.service.ProductService;

@RequestMapping("/products")
@RestController
public class ProductController {
	
	@Autowired
	ProductService productService;
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public List<Product> getAllProducts() {
		long start = System.nanoTime();
		List<Product> productList = productService.getAll();
		
		System.out.println(String.format("getAllProducts() took: %s millis", (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))));
		
		return productList;
	}
	
	@RequestMapping(value = "/allNoCache", method = RequestMethod.GET)
	public List<Product> getAllProductsNoCache() {
		long start = System.nanoTime();
		List<Product> productList = productService.getAllNoCache();
		
		System.out.println(String.format("getAllProductsNoCache() took: %s millis", (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))));
		
		return productList;
	}
	
	@RequestMapping(value = "update/{productid}/{name}", method = { RequestMethod.PUT })
	public void updateProductName(@PathVariable Integer productid, @PathVariable String name) {
		
		// use non-cached method
		Product existingProduct = productService.getProductByIdNotCached(productid);
		
		System.out.println("product old name: " + existingProduct.getName());
		Product newProduct = productService.updateProductName(productid, name);
		System.out.println("product new name: " + newProduct.getName());
	}
	
	@RequestMapping(value = "/get/{productId}", method = RequestMethod.GET)
	public Product getById(@PathVariable Integer productId) {
		long start = System.nanoTime();
		Product product = productService.getProductById(productId);
		System.out.println(String.format("getById() took: %s millis", (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))));
		
		return product;
	}
}
