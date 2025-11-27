package com.cpt.payments.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpt.payments.constant.EndPoints;
import com.cpt.payments.pojo.stripe.StripeEvent;
import com.cpt.payments.service.interfaces.StripeNotificationService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(EndPoints.STRIPE)
public class StripeNotificationController {

	@Autowired
	private HttpServletRequest httpRequest;

	@Autowired
	private StripeNotificationService stripeService;

	@Autowired
	private Gson gson;

	@Value("${stripe.notification.signing-secret}")
	private String endpointSecret;

	@PostMapping(EndPoints.NOTIFICATION)
	public ResponseEntity<String> processNotification() {

		System.out.println("StripeNotificationController.processNotification()" + "|httpRequest:" + httpRequest);

		String reqAsString;
		try {
			reqAsString = StreamUtils.copyToString(httpRequest.getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// System.out.println("Incoming request payload|reqAsString:" + reqAsString);

		String sigHeader = httpRequest.getHeader("Stripe-Signature");

		System.out.println("Incoming |sigHeader:" + sigHeader);

		// String endpointSecret = "";

		try {
			Webhook.constructEvent(reqAsString, sigHeader, endpointSecret);
			System.out.println("HMACSHA256 SIGNATURE VERIFIED!");
		} catch (JsonSyntaxException e) {
			System.out.println(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (SignatureVerificationException e) {
			System.out.println(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		System.out.println("Signature VALID, continue further processing of event");

		try {
			StripeEvent event = gson.fromJson(reqAsString, StripeEvent.class);
			System.out.println("Incoming event:" + event);

			// TODO convert to DTO & pass DTO to service layer.

			stripeService.processStripeEvent(event);
			return ResponseEntity.ok().build();

		} catch (Exception e) {
			// Handle exception
			return ResponseEntity.status(500).build();
		}

	}

}
