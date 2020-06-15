package com.arunx.bootcamp.lambda.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamEx1 {

	public void test() {
		List<String> list = new ArrayList<String>();

		list.add("A B C");
		list.add("D E F");
		list.add("G H I");
		
		//TODO: filer()
		System.out.println("\n==> filter() ====");
		Stream<String> streamFilter = list.stream();
		streamFilter.filter(v -> v.startsWith("A"))
								.forEach(System.out::println);
		
		// TODO: map()
		System.out.println("\n==> map() ====");
		Stream<String> streamMap = list.stream();
		streamMap.map(v -> v.toLowerCase()).forEach(System.out::println);
		
		// TODO: map()
		System.out.println("\n==> flatMap() ====");
		Stream<String> streamFlatMap = list.stream();
		streamFlatMap.flatMap(v -> Arrays.asList(v.split(" ")).stream())
				.forEach(System.out::println);
		
		// TODO: distinct()
		System.out.println("\n==> distinct() ====");
		Stream<String> streamDistinct = list.stream();
		List<String> distinctStrings = streamDistinct
		        .distinct()
		        .collect(Collectors.toList());
		System.out.println(distinctStrings);
		
		// TODO: limit()
		System.out.println("\n==> limit() ====");
		Stream<String> streamLimit = list.stream();
		streamLimit.limit(2).forEach(System.out::println);
		
		// TODO: peek()
		System.out.println("\n==> peek() ====");
		Stream<String> streamPeek = list.stream();
		streamPeek.peek(v -> System.out.println("v"));
	
		//TODO: anyMatch()
		System.out.println("\n==> anyMatch() ====");
		Stream<String> streamAnyMatch = list.stream();
		boolean anyMatch = streamAnyMatch.anyMatch(value -> value.startsWith("A"));
		System.out.println(anyMatch);
		
		//TODO: allMatch()
		System.out.println("\n==> allMatch() ====");
		Stream<String> streamAllMatch = list.stream();
		boolean allMatch = streamAllMatch.allMatch(value -> value.startsWith("A"));
		System.out.println(allMatch);
		
		// TODO: nonMatch()
		System.out.println("\n==> noneMatch() ====");
		Stream<String> streamNonMatch = list.stream();
		boolean noneMatch = streamNonMatch.noneMatch(value -> value.startsWith("A"));
		System.out.println(noneMatch);

		// TODO: collect()
		System.out.println("\n==> collect() ====");
		Stream<String> streamCollect = list.stream();

		List<String> listCollect = streamCollect
				.map(value -> value.toUpperCase())
				.collect(Collectors.toList());

		System.out.println(listCollect);
		
		// TODO: count()
		System.out.println("\n==> count() ====");
		Stream<String> streamCount = list.stream();
		long count = streamCount
				.flatMap(v -> Arrays.asList(v.split(" ")).stream())
				.count();
		System.out.println("count = " + count);
		
		// TODO: findAny()
		System.out.println("\n==> findAny() ====");
		Stream<String> streamFindAny = list.stream();
		Optional<String> anyElement = streamFindAny
				.flatMap(v -> Arrays.asList(v.split(" ")).stream())
				.findAny();
		System.out.println(anyElement.get());
		
		// TODO: findAny()
		System.out.println("\n==> findFirst() ====");
		Stream<String> streamFindFirst = list.stream();
		Optional<String> result = streamFindFirst.findFirst();
		System.out.println(result.isPresent());
		
		
		// TODO: forEach()
		System.out.println("\n==> forEach() ====");
		Stream<String> streamForEach = list.stream();
		streamForEach.flatMap(v -> Arrays.asList(v.split(" ")).stream())
									.forEach(System.out::print);

		// TODO: min()
		System.out.println("\n\n==> min() ====");
		Stream<String> streamMin = list.stream();
		Optional<String> min = streamMin.min((v1, v2) -> v1.compareTo(v2));
		System.out.println(min.get());
		
		// TODO: max()
		System.out.println("\n==> max() ====");
		Stream<String> streamMax = list.stream();
		Optional<String> max = streamMax.max((v1, v2) -> v1.compareTo(v2));
		System.out.println(max.get());	
		
		// TODO: reduce()
		System.out.println("\n==> reduce() ====");
		Stream<String> streamReduce = list.stream();
		Optional<String> reduced = streamReduce
				.reduce((value, accumlator) -> accumlator + " + " + value);
		System.out.println(reduced.get());
		
		// TODO: toArray()
		System.out.println("\n==> toArray() ====");
		Stream<String> streamToArray = list.stream();
		Object[] objects = streamToArray.toArray();
		Arrays.asList(objects).forEach(System.out::println);
		
		// TODO: concat()
		System.out.println("\n==> concat() ====");
		List<String> list2 = new ArrayList<>();
		list2.add("G H I");
		list2.add("J K L");
		Stream<String> stream1 = list.stream();
		Stream<String> stream2 = list2.stream();
		Stream<String> streamConcat = Stream.concat(stream1, stream2);
		System.out.println(streamConcat.collect(Collectors.toList()));
		
		// TODO: of()
		System.out.println("\n==> of() ====");
		Stream<String> streamOf = Stream.of("one", "two", "three");
		streamOf.forEach(System.out::println);
	}
	
	
}
