package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Iterator;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.example.demo.dao.UserRepository;
import com.example.demo.model.User;
import com.example.demo.model.UserAggr;

/**
 * Test - main logic for this test application
 * 
 * @author legioner
 *
 */
@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private MongoTemplate	mongoTemplate;
	
	@Test
	void contextLoads() {
		//Clear test collection 
		repository.deleteAll();
		
		//Add new document
		User user = new User();
		user.setFirstName("Jhon");
		repository.insert(user);
		
		User user_ = new User();
		user_.setId(user.getId());
		Example<User> example = Example.of(user_);
		
		//Check find in collection
		Optional<User> test = repository.findOne(example);
		assertNotNull(test);
		assertEquals(true, test.isPresent());
		assertEquals("Jhon", test.get().getFirstName());
		
		user.setLastName("Doe");
		repository.save(user);

		//Check aggregate - from several users only two have matched lastname, need to find them and their average age
		//Create users for test
		user = new User();
		user.setFirstName("Иван");
		user.setLastName("Петров");
		user.setAge(25);
		repository.insert(user);

		user = new User();
		user.setFirstName("Пётр");
		user.setLastName("Иванов");
		user.setAge(25);
		repository.insert(user);

		user = new User();
		user.setFirstName("Сидор");
		user.setLastName("Иванов");
		user.setAge(28);
		repository.insert(user);
		
		//Средний возраст всех пользователей с фамилией Иванов c проекцией Ф + И
		//First stage - match
		MatchOperation matchStage = Aggregation.match(new Criteria("lastName").is("Иванов"));
		//Last stage - project
		ProjectionOperation projectStage = Aggregation.project("names", "avgAge")
							.andExpression("concat(_id,' ',names)").as("fullName")
							;
		
		//Second stage - group (get average age, array of names)
		GroupOperation groupStage = Aggregation.group("lastName").avg("age").as("avgAge").addToSet("firstName").as("names");
		//Third stage - unwind - array of names to lastname + firstname
		UnwindOperation unwindStage = Aggregation.unwind("names");

		//Aggregation
		Aggregation aggregation = Aggregation.newAggregation(matchStage, groupStage, unwindStage, projectStage);
		 
		AggregationResults<UserAggr> output 
		  = mongoTemplate.aggregate(aggregation, "user", UserAggr.class);

		//Test 
		Iterator<UserAggr> iter = output.iterator();
		while (iter.hasNext()) {
			UserAggr test_ = iter.next();
			assertEquals(26.5f, test_.getAvgAge());
			System.out.println(test_.toString());	
		}

	}

}
