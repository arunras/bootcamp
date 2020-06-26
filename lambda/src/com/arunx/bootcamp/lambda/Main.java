package com.arunx.bootcamp.lambda;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.arunx.bootcamp.lambda.stream.StreamEx1;

public class Main {

  public static void main(String[] args) throws IOException {
    
  	testMap();
  	
  }
  
  public static void testMap() {
  	Map<String, String> map = new HashMap<>();
  	map.put("1", "one");
  	map.put("2", "two");
  	map.put("3", "three");
  	map.put("4", "four");
  	map.put("5", "five");
  	map.put("6", "six");
  	map.put("7", "seven");
  	
  	int j = 1;
  	int size = 5;
  	
  	for (int i = 0; i < map.size(); i++) {
  		//System.out.println("Key: " + map.K + ", Value: " + value);
  	}
  	
  	
  	map.forEach((key, value) -> {
  		System.out.println("Key: " + key + ", Value: " + value);
  	});
  }

}





