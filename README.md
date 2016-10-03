# boot-1.4.1-ehcache3-demo-Product-annotations

## simple demo showing how boot 1.4.1 does not work with ehcache 3.1.2 using annotations.

demo:

application.properties includes: `spring.cache.jcache.config=ehcache3.xml`

`ehcache3.xml` contains very basic ehcache configuration:
```
<config xmlns='http://www.ehcache.org/v3'
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:jsr107="http://www.ehcache.org/v3/jsr107"
		xsi:schemaLocation="http://www.ehcache.org/v3 http://www.ehcache.org/schema/ehcache-core-3.0.xsd
							http://www.ehcache.org/v3/jsr107 http://www.ehcache.org/schema/ehcache-107-ext-3.0.xsd">
	<cache alias="productCache">
		<expiry>
			<ttl unit="seconds">5</ttl>
		</expiry>
		<heap unit="entries">200</heap>
		<jsr107:mbeans enable-statistics="true"/>
	</cache>

</config>
```

run the application in debug mode

* you won't see any reference to ehcache3.xml file being loaded (the configuration). Expecting something link `Loading Ehcache XML configuration from /.../boot-1.4.1-ehcache-3.1.2-demo-Product-programmatic/target/classes/ehcache3.xml.`
* part of the console output:
```
...
Negative matches:
-----------------
...
EhCacheCacheConfiguration did not match
      - @ConditionalOnClass did not find required classes 'net.sf.ehcache.Cache', 'org.springframework.cache.ehcache.EhCacheCacheManager' (OnClassCondition)
...
```
note `net.sf.ehcache.Cache` which is the old (ehcache 2.x) namespace

* as described above, there is a 5 second expiry set in ehcache3.xml. When you hit the url http://localhost:8080/products/all you'll see that the initial 'hit' takes ~ 500 millis; this is expected. THen you hit this url immediately after, you'll see that it took ~ 0  millis; which is also expected. When you wait for more than 5 seconds, however, you'll notice that the requrest took ~ 0 millis; which is incorrect. This suggests that the xml configuration is not being loaded/used correctly.

* created a test (ApplicationTest#validateCacheExpiresEvery5Seconds()) which checks
to see that after a 6 second ‘sleep’ (1 second longer than expiry which is set to 5 seconds), the ProductServiceImpl#getProductById()  is NOT called. The test passes!?

* using Postman & method ProductServiceImpl#updateProductName to update (annotated w/ `@CachePut`) product’s ‘1’ name from ‘one’ to ‘one-test’. waiting 10+ seconds to call ProductController#getAllProducts (which, in turn, calls the service method getAll() which is annotated w/ `@Cacheable`) & the returned list does NOT include this change -> cached result even though config. states cache expires in 5 seconds & update method annotated with CachePut. When calling the service’s non-cached method, the updated product name is displayed
