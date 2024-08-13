package com.keysync.keysync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KeysyncApplication {

	public static void main(String[] args) {
		new ClientGUI();
		SpringApplication.run(KeysyncApplication.class, args);
		
	}

}
