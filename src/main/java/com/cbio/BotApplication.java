package com.cbio;

import com.cbio.app.entities.PlanEntity;
import com.cbio.app.repository.PlanRepository;
import com.cbio.core.v1.dto.ProductDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableAsync
public class BotApplication {

	private final PlanRepository planRepository;

	public BotApplication(PlanRepository planRepository) {
		this.planRepository = planRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(BotApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@PostConstruct
	public void init() {

		List<PlanEntity> products = new ArrayList<>();

		if(planRepository.count() == 0){
			PlanEntity plan = new PlanEntity();
			ProductDTO.DefaultPrice samplePrice = new ProductDTO.DefaultPrice();
			ProductDTO product = new ProductDTO();

			product.setName("Plano Basic");
			product.setId("plbasic");
			plan.setType("plbasic");
			samplePrice.setCurrency("brl");
			samplePrice.setUnitAmountDecimal(BigDecimal.valueOf(59.90));
			product.setDefaultPrice(samplePrice);
			plan.setProduct(product);
			products.add(plan);

			plan = new PlanEntity();
			samplePrice = new ProductDTO.DefaultPrice();
			product = new ProductDTO();

			product.setName("Plano Intemediate");
			product.setId("plintermediate");
			plan.setType("plintermediate");
			samplePrice.setCurrency("brl");
			samplePrice.setUnitAmountDecimal(BigDecimal.valueOf(89.90));
			product.setDefaultPrice(samplePrice);
			plan.setProduct(product);
			products.add(plan);


			plan = new PlanEntity();
			samplePrice = new ProductDTO.DefaultPrice();
			product = new ProductDTO();

			product.setName("Plano Master");
			product.setId("plmaster");
			plan.setType("plmaster");
			samplePrice.setCurrency("brl");
			samplePrice.setUnitAmountDecimal(BigDecimal.valueOf(129.90));
			product.setDefaultPrice(samplePrice);
			plan.setProduct(product);
			products.add(plan);


			System.out.println("Aplicação iniciada com sucesso!");

			planRepository.saveAll(products);
		}

	}
}
