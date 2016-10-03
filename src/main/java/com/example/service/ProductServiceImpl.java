package com.example.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.example.pojo.Product;

@Component
@CacheConfig(cacheNames = "productCache")
public class ProductServiceImpl implements ProductService {

	ConcurrentHashMap<Integer, Product> productMap = new ConcurrentHashMap<>();
	
	volatile AtomicInteger mapIndex = new AtomicInteger(0);
	private int getByIdCalledCounter = 0;
	
	@PostConstruct
	void initMap() {
		productMap.put(mapIndex.incrementAndGet(), new Product("one", 11.1));
		productMap.put(mapIndex.incrementAndGet(), new Product("two", 22.2));
		productMap.put(mapIndex.incrementAndGet(), new Product("three", 33.3));
		productMap.put(mapIndex.incrementAndGet(), new Product("four", 44.4));
		productMap.put(mapIndex.incrementAndGet(), new Product("five", 55.5));
	}
	
	@Override
	public Product getByName(String name) {
		makeSlow();
		return productMap.get(name);
	}

	@Cacheable
	@Override
	public List<Product> getAll() {
		makeSlow();
		return new ArrayList<Product>(productMap.values());
	}
	
	@Override
	public List<Product> getAllNoCache() {
		makeSlow();
		return new ArrayList<Product>(productMap.values());
	}

	@Override
	public void addProduct(Product newProduct) {
		productMap.put(mapIndex.incrementAndGet(), newProduct);
	}
	
	@CachePut
	@Override
	public Product updateProductName(Integer productId, String newName) {
		// get product key
		Iterator<ConcurrentHashMap.Entry<Integer, Product>> mapIterator = productMap.entrySet().iterator();
		Integer productKey = null;
		while(mapIterator.hasNext()) {
			ConcurrentHashMap.Entry<Integer, Product> entry = mapIterator.next();
			if(entry.getKey().equals(productId)) {
				productKey = entry.getKey();
				break;
			}
		}
		
		if(null == productKey) {
			System.err.println("product " + productId.toString() + " cannot be updated b/c it does not exist");
			
			return null;
		} else {
			// get existing product
			Product existingProduct = productMap.get(productKey);
			Product newProduct = new Product(newName, existingProduct.getPrice());
			
			// replace old product w/ new one
			productMap.replace(productKey, newProduct);
						
			return newProduct;
		}
	}
	
	@Cacheable
	@Override
	public Product getProductById(Integer productId) {
		++getByIdCalledCounter;
		System.out.println("getProductById counter: " + getByIdCalledCounter);
		
		makeSlow();
		if(!productMap.containsKey(productId)) {
			System.err.println("product id " + productId.intValue() + " does not exists");
			return null;
		} else {
			return productMap.get(productId);
		}
	}
	
	// NOT cached version of method above
	@Override
	public Product getProductByIdNotCached(Integer productId) {
		if(!productMap.containsKey(productId)) {
			System.err.println("product id " + productId.intValue() + " does not exists");
			return null;
		} else {
			return productMap.get(productId);
		}
	}
	
	@Override
	public int getGetByIdCounter() {
		return getByIdCalledCounter;
	}
	
	private void makeSlow() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.err.println("error waiting ... ");
		}
	}
}
