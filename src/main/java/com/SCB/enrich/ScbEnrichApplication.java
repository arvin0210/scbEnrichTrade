package com.SCB.enrich;

import com.SCB.enrich.Service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;

@SpringBootApplication
public class ScbEnrichApplication {

	@Autowired
	private static TradingService service;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ScbEnrichApplication.class, args);
	}

}
