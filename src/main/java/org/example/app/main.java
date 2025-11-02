package org.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class main {

	public static void main(String[] args) throws Exception {
		greetMsg();
		SpringApplication.run(main.class, args);
	}

	private static void greetMsg(){
		String msg = System.getenv().getOrDefault("GREETING_MSG", "");
		if(!msg.isEmpty()) {
			System.out.println(msg);
		}
	}
}
