package com.arunx.bootcamp.lambda.example;

public class LambdaEx1 {

	public void compare2Values() {

		MyFunction myFunction = (text1, text2) -> {
			System.out.println(text1 + text2);
			return text1 + " + " + text2;
		};
		
		String returnValue = myFunction.apply("Hellow Function Body", " Test");
		System.out.println(returnValue);
		
		MyFunction myFunction2 = myFunction;
		String returnValue2= myFunction2.apply("TEXT1" , "TEXT2");
		
		System.out.println(returnValue2);
	
		
	}
	
}
