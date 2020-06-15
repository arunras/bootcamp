package com.bootcamp.relationaldataaccess;

public class Customer {
	private long id;
	private String firstName;
	private String lastName;
	
	public Customer(long id, String firstName, String lastName) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	@Override
	public String toString() {
		return String.format(
				"Customer[id=%d, firstName='%s', lastName='%s']",
				id, firstName, lastName);
	}
	
}
