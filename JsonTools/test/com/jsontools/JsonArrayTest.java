package com.jsontools;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonArrayTest {

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
			String jsonString = "{\"set\":[1,2,3,4],\"subset\":[1,2,3]}";
			JsonObject jsonData = JsonParser.parseObjectString(jsonString);
			JsonArray set = (JsonArray) jsonData.get("set");
			JsonArray subset = (JsonArray) jsonData.get("subset");
			assertTrue(subset.subsetOf(set));
			subset.add(5L);
			assertFalse(subset.subsetOf(set));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected exception");
		}
	}
	
	@Test
	@DisplayName("subset of arrays")
	void test002() {
		try {
			String jsonString = "{\"set\":[[1],[2],[3],[4]],\"subset\":[[1],[2],[3]]}";
			JsonObject jsonData = JsonParser.parseObjectString(jsonString);
			JsonArray set = (JsonArray) jsonData.get("set");
			JsonArray subset = (JsonArray) jsonData.get("subset");
			assertTrue(subset.subsetOf(set));
			JsonArray newMember = new JsonArray();
			newMember.add(5L);
			subset.add(newMember);
			assertFalse(subset.subsetOf(set));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected exception");
		}
	}

	@Test
	@DisplayName("subset of objects")
	void test003() {
		try {
			String jsonString = "{\"set\":[{\"key1\":1},{\"key2\":2}],\"subset\":[{\"key2\":2}]}";
			JsonObject jsonData = JsonParser.parseObjectString(jsonString);
			JsonArray set = (JsonArray) jsonData.get("set");
			JsonArray subset = (JsonArray) jsonData.get("subset");
			assertTrue(subset.subsetOf(set));
			
			JsonObject newMember = new JsonObject();
			
			// check for fail with differing values
			newMember.put("key1", 3L);
			subset.add(newMember);
			assertFalse(subset.subsetOf(set));
			
			// check for fail with absent keys
			newMember.remove("key1");
			newMember.put("key3", 3L);
			assertFalse(subset.subsetOf(set));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected exception");
		}
	}
	
}
