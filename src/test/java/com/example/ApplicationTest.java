package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.pojo.Product;
import com.example.service.ProductService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private ProductService productService;
	
	@Test
	public void validateCache() {
		Integer productOneId = Integer.valueOf(1);
		
		Cache productCache = this.cacheManager.getCache("productCache");
		assertThat(productCache).isNotNull();
		productCache.clear();
		
		assertThat(productCache.get(productOneId)).isNull();
		Product productOne = productService.getProductById(productOneId);
		assertThat(productOne).isNotNull();
		
		assertThat((Product) productCache.get(productOneId).get()).isEqualTo(productOne);
	}
	
	@Test
	public void validateCacheExpiresEvery5Seconds() throws Exception {
		Integer productOneId = Integer.valueOf(1);
		
		Cache productCache = this.cacheManager.getCache("productCache");
		assertThat(productCache).isNotNull();
		productCache.clear();
		
		assertThat(productCache.get(productOneId)).isNull();
		
		// ensure that productService#getProductById was not called
		assertThat(productService.getGetByIdCounter() == 0);
		
		// call the service & populate the cache
		Product productOne = productService.getProductById(productOneId);
		assertThat(productOne).isNotNull();
		// ensure service was called 1 time
		assertThat(productService.getGetByIdCounter() == 1);
		
		// get the record & ensure that service was not called ... counter == 1
		productService.getProductById(productOneId);
		assertThat(productOne).isNotNull();
		assertThat(productService.getGetByIdCounter() == 1);
		
		// start sleep for 3 seconds (method should NOT be called, instead, cached result returned)
		System.out.println("waiting 3 seconds ...");
		CountDownLatch threeSecondLatch = new CountDownLatch(3);
		threeSecondLatch.await(3l, TimeUnit.SECONDS);
		// call service again
		productService.getProductById(productOneId);
		// ensure it was NOT called (result comes from cache) ... counter == 1
		assertThat(productService.getGetByIdCounter() == 1);
		
		// start times for 6 seconds (config. is 5 seconds)
		System.out.println("waiting 6 seconds ...");
		CountDownLatch sixSecondLatch = new CountDownLatch(6);
		sixSecondLatch.await(6l, TimeUnit.SECONDS);
		// call service again
		productService.getProductById(productOneId);
		// ensure it was called ... counter == 2
		assertThat(productService.getGetByIdCounter() == 2);
	}
}
