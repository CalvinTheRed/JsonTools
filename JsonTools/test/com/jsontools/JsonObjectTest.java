package com.jsontools;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonObjectTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	@DisplayName("subset of primitives")
	void test001() {
		try {
			JsonObject set = JsonParser.parseObjectString("{\"key1\":1,\"key2\":2}");
			JsonObject subset = JsonParser.parseObjectString("{\"key1\":1}");
			assertTrue(subset.subsetOf(set));
			
			// test fail if values do not match
			subset.put("key2", 3);
			assertFalse(subset.subsetOf(set));
			subset.remove("key2");
			
			// test fail if key is not present in set
			subset.put("key3", 3);
			assertFalse(subset.subsetOf(set));
			
		} catch (Exception e) {
			fail("Unexpected exception");
		}
	}
	
	@Test
	@DisplayName("subset of lists")
	void test002() {
		try {
			JsonObject set = JsonParser.parseObjectString("{\"key1\":[1],\"key2\":[2]}");
			JsonObject subset = JsonParser.parseObjectString("{\"key1\":[1]}");
			assertTrue(subset.subsetOf(set));
			
			JsonArray newList;
			
			// test fail if values do not match
			newList = new JsonArray();
			newList.add(3);
			subset.put("key2", newList);
			assertFalse(subset.subsetOf(set));
			subset.remove("key2");
			
			// test fail if key is not present in set
			newList = new JsonArray();
			newList.add(3);
			subset.put("key3", newList);
			assertFalse(subset.subsetOf(set));
			
		} catch (Exception e) {
			fail("Unexpected exception");
		}
	}
	
	@Test
	@DisplayName("subset of objects")
	void test003() {
		try {
			JsonObject set = JsonParser.parseObjectString("{\"key1\":{\"sub1\":1,\"sub2\":2},\"key2\":{\"sub3\":3,\"sub4\":4}}");
			JsonObject subset = JsonParser.parseObjectString("{\"key1\":{\"sub1\":1}}");
			assertTrue(subset.subsetOf(set));
			
			// test fail if values do not match
			subset.put("key2", 2);
			assertFalse(subset.subsetOf(set));
			
			// test fail if key is not present in set
			subset.put("key2", JsonParser.parseObjectString("{\"key5\":5}"));
			assertFalse(subset.subsetOf(set));
			
		} catch (Exception e) {
			fail("Unexpected exception");
		}
	}
	
	@Test
	@DisplayName("Seek test")
	void test004() {
		try {
			JsonObject data = JsonParser.parseObjectFile("resources/json_parser_test.json");
			assertEquals("general:assisted", data.seek("options[{\"hint\":\"Assist\"}].subevents[0].effect").toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
