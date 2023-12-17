package org.example.app;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

	@RequestMapping("/")
	public String index() {
		return "Hello World!";
	}


	@RequestMapping("/welcome")
	public String welcome(){
		String msg = "";
		try{
			msg = System.getenv("GREETING_MSG");
		} catch (Exception e){
			System.out.println("No env var called 'GREETING_MSG'");
		}
		if(msg != null && !msg.isEmpty()){
			System.out.println(msg);
		}
		return msg;
	}
}