package task2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Fine {

    //date format in json file
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //main fields that will be used in statistic counting
    private final DrivingViolationType violationType;
    private final double fineAmount;

    //fields that will not be serialized with xml_mapper
    @JsonIgnore
    private String name;
    @JsonIgnore
    private String lastName;
    @JsonIgnore
    private Date date;


    //constructor with main info about fine

    public Fine(DrivingViolationType drivingViolationType, double fineAmount){
        this.violationType = drivingViolationType;
        this.fineAmount = fineAmount;

    }

    //constructor with full info about fine (including fields that are not used in task)
    public Fine(String name, String lastName, Date date, DrivingViolationType violationType, double fineAmount) {
        this.name = name;
        this.lastName = lastName;
        this.date = date;
        this.fineAmount = fineAmount;
        this.violationType = violationType;
    }

    //method to get Fine object from JSON string
    public static Fine fromJson (ObjectMapper objectMapper, String jsonString) throws JsonProcessingException, ParseException {
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        String type = jsonNode.get("type").asText();
        String fineAmount = jsonNode.get("fine_amount").asText();
        String name = jsonNode.get("first_name").asText();
        String lastName = jsonNode.get("last_name").asText();
        String date = jsonNode.get("date_time").asText();
            return new Fine(name, lastName, DATE_FORMAT.parse(date), DrivingViolationType.valueOf(type), Double.parseDouble(fineAmount));
    }

    public DrivingViolationType getViolationType() {
        return violationType;
    }

    public double getFineAmount() { return fineAmount; }

    public String getName() {return name;}

    public String getLastName() {
        return lastName;
    }

    public Date getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        if (name==null || lastName==null || date == null){
            return violationType + " fine, sum is = " + fineAmount;
        } else {
            return name + " " + lastName + " GOT :" + violationType + " fine, sum is = " + fineAmount + ", date of violation: " + DATE_FORMAT.format(date);
        }
    }
}
