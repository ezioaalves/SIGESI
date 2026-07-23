package com.sigesi.sigesi;

import com.sigesi.sigesi.storage.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

@SpringBootApplication
public class SigesiApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(SigesiApplication.class);

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		SpringApplication.run(SigesiApplication.class, args);
	}

	/**
	 * Initialize MinIO bucket on startup.
	 */
	@Bean
	@ConditionalOnProperty(name = "minio.init-on-startup", havingValue = "true", matchIfMissing = true)
	public CommandLineRunner initMinio(MinioService minioService) {
		return args -> {
			try {
				minioService.initBucket();
				LOGGER.info("MinIO bucket initialized successfully");
			} catch (Exception e) {
				LOGGER.error("Failed to initialize MinIO bucket: {}", e.getMessage());
			}
		};
	}

}
