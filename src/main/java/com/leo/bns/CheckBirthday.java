package com.leo.bns;

import com.leo.bns.exception.DataProcessingException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckBirthday {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckBirthday.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int NOTIFICATION_DAYS_BEFORE = 2;
    
    @Autowired
    private EmailService emailService;
    public JSONObject readJSONFromFile(String filePath) {
        logger.debug("Reading JSON from file: {}", filePath);
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(filePath)) {
            Object obj = jsonParser.parse(fileReader);
            logger.debug("Successfully read JSON file");
            return (JSONObject) obj;
        } catch (IOException e) {
            logger.error("IO error while reading JSON file: {}", filePath, e);
            throw new DataProcessingException("Failed to read data file", e);
        } catch (ParseException e) {
            logger.error("Parse error while reading JSON file: {}", filePath, e);
            throw new DataProcessingException("Invalid data file format", e);
        }
    }


    public List<Map<String, String>> getUpcomingBirthdays(JSONObject jsonData) {
        logger.debug("Checking for upcoming birthdays");
        List<Map<String, String>> upcomingBirthdays = new ArrayList<>();

        JSONArray persons = (JSONArray) jsonData.get("members");
        if (persons == null) {
            logger.warn("No members found in data");
            return upcomingBirthdays;
        }

        LocalDate currentDate = LocalDate.now();
        logger.debug("Current date: {}", currentDate);

        for (Object personObj : persons) {
            try {
                JSONObject person = (JSONObject) personObj;
                String dobString = (String) person.get("dateOfBirth");
                
                if (dobString == null || dobString.trim().isEmpty()) {
                    logger.warn("Skipping member with missing date of birth: {}", person.get("name"));
                    continue;
                }

                LocalDate dob = parseDate(dobString);
                LocalDate notificationDate = dob.minusDays(NOTIFICATION_DAYS_BEFORE);
                
                if (isDateMatch(currentDate, notificationDate)) {
                    String name = (String) person.get("name");
                    String position = (String) person.get("position");
                    
                    Map<String, String> birthdayInfo = new HashMap<>();
                    birthdayInfo.put("name", name != null ? name : "Unknown");
                    birthdayInfo.put("position", position != null ? position : "Unknown");
                    birthdayInfo.put("birthday", dobString);
                    
                    upcomingBirthdays.add(birthdayInfo);
                    logger.info("Found upcoming birthday: {} ({})", name, position);
                }
            } catch (Exception e) {
                logger.error("Error processing member data: {}", personObj, e);
            }
        }

        logger.info("Found {} upcoming birthdays", upcomingBirthdays.size());
        return upcomingBirthdays;
    }
    
    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format: {}", dateString, e);
            throw new DataProcessingException("Invalid date format: " + dateString);
        }
    }
    
    private boolean isDateMatch(LocalDate currentDate, LocalDate targetDate) {
        return currentDate.getMonthValue() == targetDate.getMonthValue() &&
               currentDate.getDayOfMonth() == targetDate.getDayOfMonth();
    }




    public void notification() {
        logger.info("Starting birthday notification check");
        try {
            JSONObject jsonData = readJSONFromFile("data.json");
            List<Map<String, String>> upcomingBirthdays = getUpcomingBirthdays(jsonData);
            
            if (!upcomingBirthdays.isEmpty()) {
                logger.info("Sending notifications for {} upcoming birthdays", upcomingBirthdays.size());
                emailService.sendEmailNotification(upcomingBirthdays);
            } else {
                logger.debug("No upcoming birthdays found");
            }
        } catch (Exception e) {
            logger.error("Error during birthday notification process", e);
            throw e; // Re-throw to be handled by global exception handler
        }
    }
}
