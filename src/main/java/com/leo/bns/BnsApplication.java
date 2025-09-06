package com.leo.bns;

import com.leo.bns.dto.ApiResponse;
import com.leo.bns.service.AuthenticationService;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.json.simple.JSONObject;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;
import com.opencsv.CSVReader;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@RestController()
@RequestMapping("/api")
public class BnsApplication {

	private static final Logger logger = LoggerFactory.getLogger(BnsApplication.class);

	@Autowired
	private CheckBirthday birthdayService;

	@Autowired
	private AuthenticationService authService;

	@Autowired
	private SendSMS smsService;

	public static void main(String[] args) {
		SpringApplication.run(BnsApplication.class, args);
		logger.info("Birthday Notification System started successfully");
	}

	@Scheduled(cron = "0 0 12 * * *") // Daily at 12:00 PM
	@GetMapping("notification")
	public ResponseEntity<ApiResponse<String>> scheduleBirthdayNotificationAM() {
		logger.info("Executing scheduled birthday notification (AM)");
		try {
			birthdayService.notification();
			return ResponseEntity.ok(ApiResponse.success("Birthday notifications processed successfully"));
		} catch (Exception e) {
			logger.error("Error in scheduled birthday notification (AM)", e);
			return ResponseEntity.internalServerError()
					.body(ApiResponse.error("Failed to process birthday notifications"));
		}
	}

	@Scheduled(cron = "0 0 18 * * *") // Daily at 6:00 PM
	public void scheduleBirthdayNotificationPM() {
		logger.info("Executing scheduled birthday notification (PM)");
		try {
			birthdayService.notification();
		} catch (Exception e) {
			logger.error("Error in scheduled birthday notification (PM)", e);
		}
	}

	@GetMapping("/members")
	public ResponseEntity<ApiResponse<JSONObject>> getAllMembers(@RequestParam String password) {
		try {
			authService.validateApiPassword(password);
			JSONObject jsonData = birthdayService.readJSONFromFile("data.json");
			logger.info("Retrieved member data successfully");
			return ResponseEntity.ok(ApiResponse.success("Members retrieved successfully", jsonData));
		} catch (Exception e) {
			logger.error("Error retrieving members", e);
			return ResponseEntity.badRequest()
					.body(ApiResponse.error("Failed to retrieve members: " + e.getMessage()));
		}
	}


	@PostMapping("/member")
	public ResponseEntity<ApiResponse<String>> addMember(@Valid @RequestBody MemberData memberData, @RequestParam String password) {
		try {
			authService.validateApiPassword(password);
			
			JSONObject jsonData = birthdayService.readJSONFromFile("data.json");
			JSONArray persons = (JSONArray) jsonData.get("members");
			
			// Convert MemberData to JSONObject
			JSONObject data = convertMemberDataToJson(memberData);
			persons.add(data);
			jsonData.put("members", persons);

			try (FileWriter file = new FileWriter("data.json")) {
				file.write(jsonData.toString());
				logger.info("Successfully added new member: {}", memberData.getName());
				return ResponseEntity.ok(ApiResponse.success("Member added successfully"));
			}
		} catch (Exception e) {
			logger.error("Error adding member", e);
			return ResponseEntity.badRequest()
					.body(ApiResponse.error("Failed to add member: " + e.getMessage()));
		}
	}
	
	private JSONObject convertMemberDataToJson(MemberData memberData) {
		JSONObject data = new JSONObject();
		data.put("name", memberData.getName());
		data.put("dateOfBirth", memberData.getDateOfBirth());
		data.put("email", memberData.getEmail());
		data.put("position", memberData.getPosition());
		data.put("address", memberData.getAddress());
		data.put("phoneNumber", memberData.getPhoneNumber());
		data.put("gender", memberData.getGender());
		data.put("occupation", memberData.getOccupation());
		data.put("emergencyContact", memberData.getEmergencyContact());
		data.put("skills", memberData.getSkills());
		data.put("pictures", memberData.getPictures());
		data.put("consent", memberData.getConsent());
		return data;
	}

	@PostMapping("/members")
	public ResponseEntity<ApiResponse<String>> addMembers(@RequestParam("file") MultipartFile file, @RequestParam String password) {
		try {
			authService.validateApiPassword(password);
			
			if (file.isEmpty()) {
				return ResponseEntity.badRequest()
						.body(ApiResponse.error("File is required"));
			}

			CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
			List<String[]> rows = csvReader.readAll();
			csvReader.close();

			JSONObject jsonData = new JSONObject();
			JSONArray persons = new JSONArray();
			boolean isFirstRow = true;
			int processedCount = 0;
			
			for (String[] row : rows) {
				if (isFirstRow) {
					isFirstRow = false;
					continue; // Skip header row
				}
				
				if (row.length < 12) {
					logger.warn("Skipping row with insufficient columns: {}", (Object) row);
					continue;
				}
				
				try {
					JSONObject data = new JSONObject();
					data.put("email", row[0]);
					data.put("name", row[1]);
					data.put("position", row[2]);
					data.put("gender", row[3]);
					data.put("phoneNumber", row[4]);
					data.put("address", row[5]);
					data.put("dateOfBirth", row[6]);
					data.put("occupation", row[7]);
					data.put("pictures", row[8]);
					data.put("emergencyContact", row[9]);
					data.put("skills", row[10]);
					data.put("consent", row[11]);

					persons.add(data);
					processedCount++;
				} catch (Exception e) {
					logger.warn("Error processing row: {}", (Object) row, e);
				}
			}

			jsonData.put("members", persons);

			try (FileWriter fileWriter = new FileWriter("data.json")) {
				fileWriter.write(jsonData.toString());
				logger.info("Successfully processed {} members from CSV file", processedCount);
				return ResponseEntity.ok(ApiResponse.success(
						String.format("Successfully processed %d members", processedCount)));
			}
		} catch (Exception e) {
			logger.error("Error processing CSV file", e);
			return ResponseEntity.badRequest()
					.body(ApiResponse.error("Failed to process CSV file: " + e.getMessage()));
		}
	}







	@DeleteMapping("/members")
	public ResponseEntity<ApiResponse<String>> deleteAllMembers(@RequestParam String password) {
		try {
			authService.validateApiPassword(password);
			
			JSONObject jsonData = birthdayService.readJSONFromFile("data.json");
			jsonData.put("members", new JSONArray());

			try (FileWriter file = new FileWriter("data.json")) {
				file.write(jsonData.toString());
				logger.info("Successfully deleted all members");
				return ResponseEntity.ok(ApiResponse.success("All members deleted successfully"));
			}
		} catch (Exception e) {
			logger.error("Error deleting members", e);
			return ResponseEntity.badRequest()
					.body(ApiResponse.error("Failed to delete members: " + e.getMessage()));
		}
	}
}




