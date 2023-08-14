package com.leo.bns;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.json.simple.JSONObject;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;
import com.opencsv.CSVReader;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@RestController()
@RequestMapping("/api")
public class BnsApplication {

	@Autowired
	private CheckBirthday birthdayService;

	@Autowired
	private Environment env;

	private SendSMS smsService;

	@Autowired
	public BnsApplication(SendSMS smsService) {
		this.smsService = smsService;
	}

	public static void main(String[] args) {
		SpringApplication.run(BnsApplication.class, args);
	}

	@Scheduled(cron = "0 0 12 * * *")
	@GetMapping("notification")
	public void scheduleBirthdayNotificationAM() {
		birthdayService.notification();
	}


	@Scheduled(cron = " 0 0 18 * * *")
	public void scheduleBirthdayNotificationPM() {
		birthdayService.notification();
	}

	@GetMapping("/members")
	public JSONObject getAllMembers(@RequestParam String password) {
		JSONObject jsonData = new JSONObject();
		if (!password.equals(env.getProperty("API_PASSWORD"))) {
			System.out.println("Incorrect password. Access denied.");
			return jsonData;
		}

		 jsonData = birthdayService.readJSONFromFile("data.json");
		return jsonData;
	}


	@PostMapping("/member")
	public void addMember(@RequestBody JSONObject memberData, @RequestParam String password) {

		// Check the password here
		if (!password.equals(env.getProperty("API_PASSWORD"))) {
			System.out.println("Incorrect password. Access denied.");
			return;
		}

		JSONObject jsonData = birthdayService.readJSONFromFile("data.json");

		JSONArray persons = (JSONArray) jsonData.get("members");
		JSONObject data = new JSONObject();

		String name = (String) memberData.get("name");
		String dateOfBirth = (String) memberData.get("dateOfBirth");
		String email = (String) memberData.get("email");
		String position = (String) memberData.get("position");
		String address = (String) memberData.get("address");
		String phoneNumber = (String) memberData.get("phoneNumber");
		String gender = (String) memberData.get("gender");
		String occupation = (String) memberData.get("occupation");
		String emergencyContact = (String) memberData.get("emergencyContact");
		String skills = (String) memberData.get("skills");
		String pictures = (String) memberData.get("pictures");
		String consent = (String) memberData.get("consent");

		data.put("name", name);
		data.put("dateOfBirth", dateOfBirth);
		data.put("email", email);
		data.put("position", position);
		data.put("address", address);
		data.put("phoneNumber", phoneNumber);
		data.put("gender", gender);
		data.put("occupation", occupation);
		data.put("emergencyContact", emergencyContact);
		data.put("skills", skills);
		data.put("pictures", pictures);
		data.put("consent", consent);


		persons.add(data);
		// To print in JSON format.
		jsonData.put("members", persons);

		try (FileWriter file = new FileWriter("data.json")) {
			file.write(jsonData.toString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + jsonData);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@PostMapping("/members")
	public void addMembers(@RequestParam("file") MultipartFile file, @RequestParam String password) {
		// Check the password here
		if (!password.equals(env.getProperty("API_PASSWORD"))) {
			System.out.println("Incorrect password. Access denied.");
			return;
		}

		try {
			CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
			List<String[]> rows = csvReader.readAll();
			csvReader.close();

			JSONObject jsonData = new JSONObject();
			JSONArray persons = new JSONArray();
			boolean isFirstRow = true;
			// Assuming the CSV structure: name, dateOfBirth, email, position, address, phoneNumber
			for (String[] row : rows) {
				if (isFirstRow) {
					isFirstRow = false;
					continue; // Skip the first row (headings)
				}
				JSONObject data = new JSONObject();

				String name = row[1];
				String dateOfBirth = row[6];
				String email = row[0];
				String position = row[2];
				String address = row[5];
				String phoneNumber = row[4];
				String gender = row[3];
				String occupation = row[7];
				String emergencyContact = row[9];
				String skills = row[10];
				String pictures = row[8];
				String consent = row[11];

				System.out.println(dateOfBirth);
				data.put("name", name);
				data.put("dateOfBirth", dateOfBirth);
				data.put("email", email);
				data.put("position", position);
				data.put("address", address);
				data.put("phoneNumber", phoneNumber);
				data.put("gender", gender);
				data.put("occupation", occupation);
				data.put("emergencyContact", emergencyContact);
				data.put("skills", skills);
				data.put("pictures", pictures);
				data.put("consent", consent);

				persons.add(data);
			}

			// To print in JSON format.
			jsonData.put("members", persons);

			try (FileWriter fileWriter = new FileWriter("data.json")) {
				fileWriter.write(jsonData.toString());
				System.out.println("Successfully created JSON file...");
				System.out.println("\nJSON Object: " + jsonData);
			} catch (Exception e) {
				System.out.println(e);
			}
		} catch (Exception e) {
			System.out.println("Error occurred while parsing the CSV file.");
			e.printStackTrace();
		}
	}







	@DeleteMapping("/members")
	public void deleteAllMembers(@RequestParam String password) {

		// Check the password here
		if (!password.equals(env.getProperty("API_PASSWORD"))) {
			System.out.println("Incorrect password. Access denied.");
			return;
		}
		JSONObject jsonData = birthdayService.readJSONFromFile("data.json");
		jsonData.put("members", new JSONArray());

		try (FileWriter file = new FileWriter("data.json")) {
			file.write(jsonData.toString());
			System.out.println("Successfully deleted all members...");
			System.out.println("\nJSON Object: " + jsonData);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}




