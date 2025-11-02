package org.example.app;

import java.time.LocalDateTime;

public class Transaction {
	public final String name;
	public final String drink;
	public final double price;
	public final LocalDateTime timestamp;

	public Transaction(String name, String drink, double price, LocalDateTime timestamp) {
		this.name = name;
		this.drink = drink;
		this.price = price;
		this.timestamp = timestamp;
	}
}

