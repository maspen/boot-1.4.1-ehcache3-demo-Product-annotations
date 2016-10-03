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
