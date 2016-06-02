package fr.auri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@SpringBootApplication
public class NonoApplication {

    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "Hello Nono!";
    }

	public static void main(String[] args) {
		SpringApplication.run(NonoApplication.class, args);

	}
}
