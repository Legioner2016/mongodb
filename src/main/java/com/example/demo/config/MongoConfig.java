package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoCredential;

/**
 * Mongo connection configuration
 * 
 * @author legioner
 *
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.example.demo.dao")
public class MongoConfig {

	@Bean
	public MongoClientFactoryBean mongo() {
		MongoClientFactoryBean mongo = new MongoClientFactoryBean();
		mongo.setHost("localhost");
		mongo.setPort(27017);
		MongoCredential credential = MongoCredential.createCredential("user", "examples", "pass123".toCharArray());  
		mongo.setCredential(new MongoCredential[] {credential});
		return mongo;
	}

	@Bean
	public MongoDatabaseFactory mongoDbFactory() 
	{
		try {
			return new SimpleMongoClientDatabaseFactory(mongo().getObject(), "examples");
		} catch (Exception e) {
		}
		return null;
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoDbFactory());
	}

}