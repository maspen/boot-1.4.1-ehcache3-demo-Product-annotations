package com.example.service;

import java.util.List;

import com.example.pojo.Product;

public interface ProductService {
	Product getByName(String name);
	List<Product> getAll();
	List<Product> getAllNoCache();
	void addProduct(Product newProduct);
	public Product getProductById(Integer productId);
	
	// same as above but need way to circumvent cachine
	public Product getProductByIdNotCached(Integer productId);
	
	Product updateProductName(Integer productId, String newName);
	
	// for testing
	public int getGetByIdCounter();
}
