package com.leo.bns;

import org.json.simple.JSONArray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.json.simple.JSONObject;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.FileWriter;



@SpringBootApplication
@EnableScheduling
@RestController()
@RequestMapping("/api/members")
public class BnsApplication {


	private CheckBirthday birthdayService;


	public static void main(String[] args) {
		SpringApplication.run(BnsApplication.class, args);
		CheckBirthday.notification();
	}


	@PostMapping
	public void addMember(@RequestBody JSONObject memberData) {
		JSONObject jsonData = birthdayService.readJSONFromFile("data.json");
		System.out.println(jsonData);
		JSONArray persons = (JSONArray) jsonData.get("members");
		JSONObject data = new JSONObject();

		String name = (String) memberData.get("name");
		String dateOfBirth = (String) memberData.get("dateOfBirth");

		data.put("name", name);
		data.put("dateOfBirth", dateOfBirth);

		persons.add(data);
		// To print in JSON format.
		jsonData.put("members", persons);



		try (FileWriter file = new FileWriter("data.json")) {
			file.write(jsonData.toString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + jsonData);
		} catch(Exception e){
			System.out.println(e);

		}
	}
}




