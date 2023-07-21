package com.leo.bns;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.json.simple.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;
import com.opencsv.CSVReader;

@SpringBootApplication
@EnableScheduling
@RestController()
@RequestMapping("/api")
public class BnsApplication {

	@Autowired
	private CheckBirthday birthdayService;

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(BnsApplication.class, args);
	}

	@Scheduled(cron = "0 0 6 * * *")
	@GetMapping("notification")
	public void scheduleBirthdayNotificationAM() {
		birthdayService.notification();
	}


	@Scheduled(cron = " 0 0 18 * * *")
	public void scheduleBirthdayNotificationPM() {
		birthdayService.notification();
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
		String address = (String) memberData.get("whereYouStay");
		String phoneNumber = (String) memberData.get("phoneNumber");

		data.put("name", name);
		data.put("dateOfBirth", dateOfBirth);
		data.put("email", email);
		data.put("position", position);
		data.put("address", address);
		data.put("phoneNumber", phoneNumber);

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
	public void addMembers(@RequestBody MembersRequest request, @RequestParam String password) {

		// Check the password here
		if (!password.equals(env.getProperty("API_PASSWORD"))) {
			System.out.println("Incorrect password. Access denied.");
			return;
		}

		JSONObject jsonData = birthdayService.readJSONFromFile("data.json");
		JSONArray persons = (JSONArray) jsonData.get("members");

		for (MemberData memberData : request.getMembers()) {
			JSONObject data = new JSONObject();


			String name = memberData.getName();
			String dateOfBirth = memberData.getDateOfBirth();
			String email = memberData.getEmail();
			String position = memberData.getPosition();
			String address = memberData.getWhereYouStay();
			String phoneNumber = memberData.getPhoneNumber();

			data.put("name", name);
			data.put("dateOfBirth", dateOfBirth);
			data.put("email", email);
			data.put("position", position);
			data.put("address", address);
			data.put("phoneNumber", phoneNumber);

			persons.add(data);
		}

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




