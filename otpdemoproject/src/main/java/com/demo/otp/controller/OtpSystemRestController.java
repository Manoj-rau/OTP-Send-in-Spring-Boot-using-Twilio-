package com.demo.otp.controller;

import com.demo.otp.model.OtpSystem;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OtpSystemRestController {

	private Map<String, OtpSystem> otp_data = new HashMap<>();
	private final static String ACCOUNT_SID = "ACb10205c871724e856ee082bfd2f11c7f";
	private final static String AUTH_TOKEN = "dea8bce313096e01507745f39ede7c00";

	static {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
	}

	@RequestMapping(value = "/mobileNumbers/{mobileNumber}/otp", method = RequestMethod.POST)
	public ResponseEntity<Object> sendOTP(@PathVariable("mobileNumber") String mobileNumber) {

		OtpSystem otpsystem = new OtpSystem();
		otpsystem.setMobileNumber(mobileNumber);
		otpsystem.setOtp(String.valueOf(((int) (Math.random() * (10000 - 1000))) + 1000));
		otpsystem.setExpiryTime(System.currentTimeMillis() + 40000);
		otp_data.put(mobileNumber, otpsystem);
		Message.creator(new PhoneNumber("+917000029225"), new PhoneNumber("(+12055707479"),
				"Your OTP sent by Manoj Raut is :  " + otpsystem.getOtp()).create();
		return new ResponseEntity<>("OTP sent successfully", HttpStatus.OK);

	}

	@RequestMapping(value = "/mobileNumbers/{mobileNumber}/otp", method = RequestMethod.PUT)
	public ResponseEntity<Object> verifyOTP(@PathVariable("mobileNumber") String mobileNumber,
			@RequestBody OtpSystem requestBodyOtpSystem) {
		
		if(requestBodyOtpSystem.getOtp()==null || requestBodyOtpSystem.getOtp().trim().length()<=0) {
			
			return new ResponseEntity<> ("Please provide OTP", HttpStatus.BAD_REQUEST);
		}
		

		if (otp_data.containsKey(mobileNumber)) {

			OtpSystem otpsystem = otp_data.get(mobileNumber);
			if (otpsystem != null) {
				if (otpsystem.getExpiryTime() >= System.currentTimeMillis()) {
					if (requestBodyOtpSystem.getOtp().equals(otpsystem.getOtp())) {
						
						otp_data.remove(mobileNumber);
						return new ResponseEntity<>("OTP is verified successfully", HttpStatus.OK);

					}
					return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);

				}
				return new ResponseEntity<>("OTP is expired", HttpStatus.BAD_REQUEST);

			}
			
			return new ResponseEntity<>("Something went wrong", HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Mobile number not found", HttpStatus.NOT_FOUND);
	}

}
