package org.example.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/")
	public String index() {
		return "Hello World!";
	}

	@GetMapping("/welcome")
	public String welcome() {
		String message = System.getenv().getOrDefault("GREETING_MSG", "");
		if (!message.isEmpty()) {
			System.out.println(message);
		}
		return message;
	}

	@GetMapping("/coffee")
	public String coffee() {
		return "Coffee is ready!";
	}

	@GetMapping("/tea")
	public String tea() {
		return "Tea is ready!";
	}

	@GetMapping("/water")
	public String water() {
		return "Water is ready!";
	}
}