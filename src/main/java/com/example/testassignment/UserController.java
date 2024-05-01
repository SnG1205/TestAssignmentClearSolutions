package com.example.testassignment;

import com.example.testassignment.data.ErrorMessage;
import com.example.testassignment.data.Response;
import com.example.testassignment.data.ResponseData;
import com.example.testassignment.data.User;
import com.example.testassignment.exceptions.RestResponseException;
import com.example.testassignment.util.JsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@RestController
public class UserController {

    private List<User> users = new ArrayList<>();
    private final JsonConverter jsonConverter = new JsonConverter();

    @Value("${age.limit}")
    private int age;

    @GetMapping("/users")
    public String getUsers() throws JsonProcessingException {
        Response response = new Response(users);
        return jsonConverter.toJson(response);
    }

    @GetMapping("/users/range")
    public String getUsersByBirthDateRange(
            @RequestParam(value = "from") String fromDate,
            @RequestParam(value = "to") String toDate
    ) throws JsonProcessingException {
        List<User> userList = users.stream().filter(user -> checkIfBirthDateIsInRange(user.getBirthDate(), fromDate, toDate)).toList();
        Response response = new Response(userList);
        return jsonConverter.toJson(response);
    }

    @PostMapping("/users")
    public String createUser(@RequestBody User user) throws JsonProcessingException {
        LocalDate birthDate;
        LocalDate now = LocalDate.now();
        int currentYear;
        int userAge;

        checkNulls(user);
        validateEmail(user.getEmail());
        try{
            birthDate = LocalDate.parse(user.getBirthDate()); //yyyy-MM-dd
            currentYear = now.getYear();
            if(now.getMonthValue() >  birthDate.getMonthValue()
                    || (now.getMonthValue() == birthDate.getMonthValue() && now.getDayOfMonth() > birthDate.getDayOfMonth())){
                userAge = currentYear - birthDate.getYear();
            }
            else {
                userAge = currentYear - birthDate.getYear() - 1;
            }
        }
        catch (Exception e){
            throw new RestResponseException(ErrorMessage.WRONG_DATE_FORMAT.getMessage());
        }

        if(birthDate.isAfter(now)){
            throw new RestResponseException(ErrorMessage.FUTURE_BIRTH_DATE.getMessage());
        }

        if (userAge < age){
            throw new RestResponseException(ErrorMessage.UNDER_AGE.getMessage());
        }
        else{
            replaceNullsInCreateForOptionals(user);
            users.add(user);

            ResponseData responseData = new ResponseData("User was successfully added", 201);
            Response response = new Response(responseData);
            return jsonConverter.toJson(response);
        }
    }

    @PutMapping("/users/{id}")
    public String updateUser(@PathVariable("id") int id, @RequestBody User user) throws JsonProcessingException {
        int index = id - 1;

        checkNulls(user);
        try{
            users.remove(index);
            replaceNullsInCreateForOptionals(user);
            users.add(user);
        }
        catch (Exception e){
            throw new RestResponseException(ErrorMessage.NO_ID.getMessage());
        }

        ResponseData responseData = new ResponseData("User information was successfully updated", 200);
        Response response = new Response(responseData);
        return jsonConverter.toJson(response);
    }

    @PatchMapping("/users/{id}")
    public String updateFieldsForUser(@PathVariable("id") int id, @RequestBody User user) throws JsonProcessingException {
        int index = id - 1;
        try{
            User existingUser = users.get(index);
            users.remove(index);
            users.add(existingUser.updateFields(user));
        }
        catch (Exception e){
            throw new RestResponseException(ErrorMessage.NO_ID.getMessage());
        }

        ResponseData responseData = new ResponseData("User information was successfully updated", 200);
        Response response = new Response(responseData);
        return jsonConverter.toJson(response);
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable("id") int id) throws JsonProcessingException {
        int index = id - 1;
        try{
            users.remove(index);
        }
        catch (Exception e){
            throw new RestResponseException(ErrorMessage.NO_ID.getMessage());
        }

        ResponseData responseData = new ResponseData("User was successfully deleted", 200);
        Response response = new Response(responseData);
        return jsonConverter.toJson(response);
    }

    private boolean checkIfBirthDateIsInRange(String birthDate, String fromDate, String toDate){
        LocalDate birth = LocalDate.parse(birthDate);
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        LocalDate now = LocalDate.now();

        if(from.isAfter(now)){
            throw new RestResponseException(ErrorMessage.FUTURE_FROM_DATE.getMessage());
        }
        if(to.isAfter(now)){
            throw new RestResponseException(ErrorMessage.FUTURE_TO_DATE.getMessage());
        }
        if(from.isAfter(to)){
            throw new RestResponseException(ErrorMessage.FROM_MORE_THAN_TO.getMessage());
        }

        return birth.isAfter(from) && birth.isBefore(to);
    }

    private void checkNulls(User user){
        if(user.getEmail() == null){
            throw new RestResponseException(ErrorMessage.NO_EMAIL.getMessage());
        }
        if(user.getFirstName() == null){
            throw new RestResponseException(ErrorMessage.NO_FIRST_NAME.getMessage());
        }
        if(user.getLastName() == null){
            throw new RestResponseException(ErrorMessage.NO_LAST_NAME.getMessage());
        }
        if(user.getBirthDate() == null){
            throw new RestResponseException(ErrorMessage.NO_BIRTH_DATE.getMessage());
        }
    }

    private void replaceNullsInCreateForOptionals(User user){
        if(user.getAddress() == null){
            user.setAddress("");
        }
        if(user.getPhoneNumber() == null){
            user.setPhoneNumber("");
        }
    }

    private void validateEmail(String email){ //Note: literally copied this regex expression in the internet. Did not even try doing it on my own. Took a while to understand it.
        final String pattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if(!Pattern.compile(pattern).matcher(email).matches()){
            throw new RestResponseException(ErrorMessage.INVALID_EMAIL.getMessage());
        }
    }
}
