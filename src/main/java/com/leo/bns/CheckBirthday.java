package com.leo.bns;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckBirthday {
    private SendSMS smsService;

    @Autowired
    private EmailService emailService;
    public JSONObject readJSONFromFile(String filePath) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(filePath)) {
            Object obj = jsonParser.parse(fileReader);
            return (JSONObject) obj;
        } catch (IOException | ParseException e) {
            System.out.println("An error occurred while reading the JSON file.");
            e.printStackTrace();
        }
        return null;
    }


    public List<Map<String, String>> getUpcomingBirthdays(JSONObject jsonData) {
        List<Map<String, String>> upcomingBirthdays = new ArrayList<>();

        JSONArray persons = (JSONArray) jsonData.get("members");
        LocalDate currentDate = LocalDate.now();
        System.out.println(currentDate);

        for (Object personObj : persons) {
            JSONObject person = (JSONObject) personObj;
            String dobString = (String) person.get("dateOfBirth");

            // Parse date string with custom formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dob = LocalDate.parse(dobString, formatter).minusDays(2);
            System.out.println(dob);

//            LocalDate date = currentDate;
            int dobMonth = dob.getMonthValue();
            int dobDay = dob.getDayOfMonth();
            int currentMonth = currentDate.getMonthValue();
            int currentDay = currentDate.getDayOfMonth();
            System.out.println(dobDay);
            System.out.println(currentDay);
            if (dobMonth == currentMonth && dobDay == currentDay) {
                String name = (String) person.get("name");
                String position = (String) person.get("position");
                Map<String, String> birthdayInfo = new HashMap<>();
                birthdayInfo.put("name", name);
                birthdayInfo.put("position", position);
                upcomingBirthdays.add(birthdayInfo);
            }

        }

        System.out.println(upcomingBirthdays);
        return upcomingBirthdays;
    }




    public void notification() {
        JSONObject jsonData = readJSONFromFile("data.json");
        if (jsonData != null) {
            // Check if any DOB is two days before the current date
            List<Map<String, String>> upcomingBirthdays = getUpcomingBirthdays(jsonData);
            // Send email notification
            if (!upcomingBirthdays.isEmpty()) {
                emailService.sendEmailNotification(upcomingBirthdays);
            }
        }
    }
}
