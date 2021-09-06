package com.jsontools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.PrintWriter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonParserTest {

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
	@DisplayName("Parse from file object")
	void test001() {
		try {
			// create reference file
			String filepath = "test.json";
			String jsonString = "{\"key\":\"value\"}";
			PrintWriter writer = new PrintWriter(filepath, "UTF-8");
			writer.println(jsonString);
			writer.close();
			
			// parse data from file
			File file = new File(filepath);
			JsonObject jdata = JsonParser.parseFile(file);
			
			// assertions
			assertEquals(1, jdata.keySet().size());
			assertEquals("value", jdata.get("key"));
			
			// delete reference file
			file.delete();
			
		} catch (Exception ex) {
			fail("Unexpected exception");
			ex.printStackTrace();
		}
		
	}
	
	@Test
	@DisplayName("Parse from file path")
	void test002() {
		try {
			// create reference file
			String filepath = "test.json";
			String jsonString = "{\"key\":\"value\"}";
			PrintWriter writer = new PrintWriter(filepath, "UTF-8");
			writer.println(jsonString);
			writer.close();
			
			// parse data from file
			File file = new File(filepath);
			JsonObject jdata = JsonParser.parseFile(filepath);
			
			// assertions
			assertEquals(1, jdata.keySet().size());
			assertEquals("value", jdata.get("key"));
			
			// delete reference file
			file.delete();
			
		} catch (Exception ex) {
			fail("Unexpected exception");
			ex.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Parse from json string")
	void test003() {
		try {
			// create reference string
			String jsonString = "{\"key\":\"value\"}";
			
			// parse data from string
			JsonObject jdata = JsonParser.parseString(jsonString);
			
			// assertions
			assertEquals(1, jdata.keySet().size());
			assertEquals("value", jdata.get("key"));
			
		} catch (Exception ex) {
			fail("Unexpected exception");
			ex.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Parse value (string)")
	void test004() {
		try {
			// create reference string
			String jsonString = "{\"key\":\"value\"}";
			
			// parse data from string
			JsonObject jdata = JsonParser.parseString(jsonString);
			
			// assertions
			assertEquals(1, jdata.keySet().size());
			assertEquals("value", jdata.get("key"));
			
		} catch (Exception ex) {
			fail("Unexpected exception");
			ex.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Parse value (bool)")
	void test005() {
		try {
			// create reference string
			String jsonString = "{\"key\":true}";
			
			// parse data from string
			JsonObject jdata = JsonParser.parseString(jsonString);
			
			// assertions
			assertEquals(1, jdata.keySet().size());
			Object value = jdata.get("key");
			assertTrue(value instanceof Boolean);
			assertEquals(true, jdata.get("key"));
			
		} catch (Exception ex) {
			fail("Unexpected exception");
			ex.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Parse value (long)")
	void test006() {
		try {
			// create reference string
			String jsonString = "{\"key\":20}";
			
			// parse data from string
			JsonObject jdata = JsonParser.parseString(jsonString);
			
			// assertions
			assertEquals(1, jdata.keySet().size());
			Object value = jdata.get("key");
			assertTrue(value instanceof Long);
			assertEquals(20L, jdata.get("key"));
			
		} catch (Exception ex) {
			fail("Unexpected exception");
			ex.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Parse value (double)")
	void test007() {
		try {
			// create reference string
			String jsonString = "{\"key\":2.0}";
			
			// parse data from string
			JsonObject jdata = JsonParser.parseString(jsonString);
			
			// assertions
			assertEquals(1, jdata.keySet().size());
			Object value = jdata.get("key");
			assertTrue(value instanceof Double);
			assertEquals(2.0, jdata.get("key"));
			
		} catch (Exception ex) {
			fail("Unexpected exception");
			ex.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Parse value (object)")
	void test008() {
		try {
			// create reference string
			String jsonString = "{\"key\":{\"key1\":1,\"key2\":2,\"key3\":3}}";
			
			// parse data from string
			JsonObject jdata = JsonParser.parseString(jsonString);
			
			// assertions
			assertEquals(1, jdata.keySet().size());
			Object value = jdata.get("key");
			assertTrue(value instanceof JsonObject);
			JsonObject jobj = (JsonObject) value;
			assertEquals(1L, jobj.get("key1"));
			assertEquals(2L, jobj.get("key2"));
			assertEquals(3L, jobj.get("key3"));
			
		} catch (Exception ex) {
			fail("Unexpected exception");
			ex.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Parse value (list)")
	void test009() {
		try {
			// create reference string
			String jsonString = "{\"key\":[1,2,3]}";
			
			// parse data from string
			JsonObject jdata = JsonParser.parseString(jsonString);
			
			// assertions
			assertEquals(1, jdata.keySet().size());
			Object value = jdata.get("key");
			assertTrue(value instanceof JsonList);
			JsonList jlist = (JsonList) value;
			assertEquals(1L, jlist.get(0));
			assertEquals(2L, jlist.get(1));
			assertEquals(3L, jlist.get(2));
			
		} catch (Exception ex) {
			fail("Unexpected exception");
			ex.printStackTrace();
		}
	}

}
