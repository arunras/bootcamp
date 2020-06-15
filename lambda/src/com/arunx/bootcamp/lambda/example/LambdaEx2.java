package com.arunx.bootcamp.lambda.example;

import java.io.IOException;

public class LambdaEx2 {
	
	String thirdText = " : Third text";
	String fourthText = " : Fourth Text";
	
	public void doIt() {
		String hello = "Hello: ";

		/*
		MyInterface myInterface = (text) -> {
			System.out.println(hello + text + fourthText);
		};
		*/
		MyInterface myInterface = System.out::println;
		
		myInterface.printIt("ABC");
		
		this.fourthText = " : new fourth text";
		
		myInterface.printIt("ABC");
		
	}

	public void test() throws IOException {
		LambdaEx2 lambda = new LambdaEx2();
  	lambda.doIt();
	}
	
}
