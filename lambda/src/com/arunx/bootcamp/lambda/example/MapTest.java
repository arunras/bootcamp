package com.arunx.bootcamp.lambda.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class MapTest {

	public static void map() {
    String[] array = {"Arun", "Bunthet", "Siemhong", "Khemrin"};
    Stream<String> aStream = Stream.of(array);
    aStream.map(e -> e.length())
      .forEach(e -> System.out.println(e));
  }

  public static void flatMap() {
    Path path = Paths.get("/Users/arun/Desktop/file");
    Set<String> set = new HashSet<>();
    try {
      List<String> lines = Files.readAllLines(path);
      for (String line : lines) {
        String[] words = line.split("\\s+");
        for (String word : words) {
          set.add(word);
        }
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
    System.out.println("Distinct words: " + set);


    try {
      Files.lines(path)
        .map(e -> e.split("\\s+"))
        .flatMap(Arrays::stream)
        .distinct()
        .forEach(System.out::println);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
}
