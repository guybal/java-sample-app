package org.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class exampleapp {

	public static void main(String[] args) throws Exception {
		greetMsg();
		SpringApplication.run(exampleapp.class, args);
	}

	private static void greetMsg(){
		String msg = "";
		try{
			msg = System.getenv("GREETING_MSG");
		} catch (Exception e){
			System.out.println("No env var called 'GREETING_MSG'");
		}
		if(msg != null && !msg.isEmpty()) {
			System.out.println(msg);
		}
	}
}
