package com.cpt.payments.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import com.cpt.payments.dto.TransactionDTO;
import com.cpt.payments.entity.TransactionEntity;
import com.cpt.payments.utils.PaymentMethodEnumConverter;
import com.cpt.payments.utils.PaymentMethodIdConverter;
import com.cpt.payments.utils.PaymentTypeEnumConverter;
import com.cpt.payments.utils.PaymentTypeIdConverter;
import com.cpt.payments.utils.ProviderEnumConverter;
import com.cpt.payments.utils.ProviderIdConverter;
import com.cpt.payments.utils.TxnStatusEnumConverter;
import com.cpt.payments.utils.TxnStatusIdConverter;

@Configuration
public class AppConfig {

	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	ModelMapper getModelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		Converter<String, Integer> paymentMethodEnumConverter = new PaymentMethodEnumConverter();
		Converter<String, Integer> providerEnumConverter = new ProviderEnumConverter();
		Converter<String, Integer> txnStatusEnumConverter = new TxnStatusEnumConverter();
		Converter<String, Integer> paymentTypeEnumConverter = new PaymentTypeEnumConverter();
		// Define converters for TxnStatusEnum and PaymentTypeEnum if needed

		modelMapper.addMappings(new PropertyMap<TransactionDTO, TransactionEntity>() {
			@Override
			protected void configure() {
				using(paymentMethodEnumConverter).map(source.getPaymentMethod(), destination.getPaymentMethodId());
				using(providerEnumConverter).map(source.getProvider(), destination.getProviderId());
				using(txnStatusEnumConverter).map(source.getTxnStatus(), destination.getTxnStatusId());
				using(paymentTypeEnumConverter).map(source.getPaymentType(), destination.getPaymentTypeId());
			}
		});

		Converter<Integer, String> paymentMethodIdConverter = new PaymentMethodIdConverter();
		Converter<Integer, String> providerIdConverter = new ProviderIdConverter();
		Converter<Integer, String> txnStatusIdConverter = new TxnStatusIdConverter();
		Converter<Integer, String> paymentTypeIdConverter = new PaymentTypeIdConverter();

		modelMapper.addMappings(new PropertyMap<TransactionEntity, TransactionDTO>() {
			@Override
			protected void configure() {
				using(paymentMethodIdConverter).map(source.getPaymentMethodId(), destination.getPaymentMethod());
				using(providerIdConverter).map(source.getProviderId(), destination.getProvider());
				using(txnStatusIdConverter).map(source.getTxnStatusId(), destination.getTxnStatus());
				using(paymentTypeIdConverter).map(source.getPaymentTypeId(), destination.getPaymentType());
			}
		});

		return modelMapper;
	}
	
	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		executor.setThreadNamePrefix("Async-Task-");
		executor.initialize();
		return executor;
	}

}
