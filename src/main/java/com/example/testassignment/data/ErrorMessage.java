package com.example.testassignment.data;

public enum ErrorMessage {

    WRONG_DATE_FORMAT("Birth date was entered in wrong format. Acceptable format is: yyyy-MM-dd"),
    NO_EMAIL("User`s email can not be empty"),
    NO_FIRST_NAME("User`s first name can not be empty"),
    NO_LAST_NAME("User`s last name can not be empty"),
    NO_BIRTH_DATE("User`s birth date can not be empty"),
    FUTURE_BIRTH_DATE("Birth date can not be in the future"),
    UNDER_AGE("You must be over 18 years to register"),
    NO_ID("No user with such id exists"),
    FUTURE_FROM_DATE("'From' birth date can not be in future"),
    FUTURE_TO_DATE("'To' birth date can not be in future"),
    FROM_MORE_THAN_TO("'From' birth date must be less than 'To'"),
    INVALID_EMAIL("User`s email is invalid");

    private final String message;

    ErrorMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
