package com.example.testassignment;

import com.example.testassignment.data.ErrorMessage;
import com.example.testassignment.data.User;
import com.example.testassignment.util.JsonConverter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @AfterEach
    private void clean() throws Exception {
        mvc.perform(delete("/users/1"));
    }

    private final User USER = new User("sergey.dn2003@gmail.com",
            "Serhii",
            "Holiev",
            "2003-12-05",
            "Street 1",
            "+4366499335880");
    private final JsonConverter jsonConverter = new JsonConverter();

    @Test
    public void addCorrectUserReturnsMessage() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['data']['code']", Matchers.equalTo(201)));
    }

    @Test
    public void addUserWithNoAddressAndPhoneNumber() throws Exception {
        //given
        User user = new User("sergey.dn2003@gmail.com",
                "Serhii",
                "Holiev",
                "2003-12-05",
                null,
                null);

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['data']['code']", Matchers.equalTo(201)));
    }

    @Test
    public void addUserWithNoEmail() throws Exception {
        //given
        User user = new User(null,
                "Serhii",
                "Holiev",
                "2003-12-05",
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.NO_EMAIL.getMessage())));
    }

    @Test
    public void addUserWithNoFirstName() throws Exception {
        //given
        User user = new User("sergey.dn2003@gmail.com",
                null,
                "Holiev",
                "2003-12-05",
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.NO_FIRST_NAME.getMessage())));
    }

    @Test
    public void addUserWithNoLastName() throws Exception {
        //given
        User user = new User("sergey.dn2003@gmail.com",
                "Serhii",
                null,
                "2003-12-05",
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.NO_LAST_NAME.getMessage())));
    }

    @Test
    public void addUserWithNoBirthDate() throws Exception {
        //given
        User user = new User("sergey.dn2003@gmail.com",
                "Serhii",
                "Holiev",
                null,
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.NO_BIRTH_DATE.getMessage())));
    }

    @Test
    public void addUserWithFutureBirthDate() throws Exception {
        //given
        User user = new User("sergey.dn2003@gmail.com",
                "Serhii",
                "Holiev",
                "2024-12-05",
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.FUTURE_BIRTH_DATE.getMessage())));
    }

    @Test
    public void addUserUnderAgeLimit() throws Exception {
        //given
        User user = new User("sergey.dn2003@gmail.com",
                "Serhii",
                "Holiev",
                "2010-12-05",
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.UNDER_AGE.getMessage())));
    }

    @Test
    public void addUserUnderAgeLimitButIn18thYear() throws Exception {
        //given
        User user = new User("sergey.dn2003@gmail.com",
                "Serhii",
                "Holiev",
                "2006-06-08",
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.UNDER_AGE.getMessage())));
    }

    @Test
    public void addUserOverAgeLimitIn18thYear() throws Exception {
        //given
        User user = new User("sergey.dn2003@gmail.com",
                "Serhii",
                "Holiev",
                "2006-03-01",
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['data']['code']", Matchers.equalTo(201)));
    }

    @Test
    public void addUserWithWrongBirthDateFormat() throws Exception {
        //given
        User user = new User("sergey.dn2003@gmail.com",
                "Serhii",
                "Holiev",
                "12-05-2003",
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.WRONG_DATE_FORMAT.getMessage())));
    }

    @ParameterizedTest
    @MethodSource("invalidEmails")
    public void addUserWithInvalidEmails(User user) throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.INVALID_EMAIL.getMessage())));
    }

    @Test
    public void getUserReturnsEmptyList() throws Exception {
        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$['data']", hasSize(0)));
    }

    @Test
    public void getUserReturnsListWithUser() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(USER)))
                .andExpect(status().isOk())
                .andDo(
                        result -> mvc.perform(get("/users")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$['data']", hasSize(1)))
                );
    }

    @Test
    public void deleteUser() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(USER)))
                .andExpect(status().isOk())
                .andDo(
                        result -> mvc.perform(delete("/users/1")
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$['data']['code']", Matchers.equalTo(200)))
                );
    }

    @Test
    public void deleteNotExistingUser() throws Exception {
        mvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.NO_ID.getMessage())));
    }

    @Test
    public void updateUser() throws Exception {
        //given
        User updatedUser = new User("sergey.dn2003@gmail.com",
                "Taras",
                "Shevchenko",
                "2003-12-05",
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(USER)))
                .andExpect(status().isOk())
                .andDo(
                        result -> mvc.perform(put("/users/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonConverter.toJson(updatedUser)))
                                .andExpect(jsonPath("$['data']['code']", Matchers.equalTo(200)))
                )
                .andDo(
                        result -> mvc.perform(get("/users")
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$['data']", hasSize(1)))
                                .andExpect(jsonPath("$['data'][0].firstName", equalTo("Taras")))
                                .andExpect(jsonPath("$['data'][0].lastName", equalTo("Shevchenko")))
                );
    }

    @Test
    public void updateNotExistingUser() throws Exception {
        //given
        User updatedUser = new User("sergey.dn2003@gmail.com",
                "Taras",
                "Shevchenko",
                "2003-12-05",
                "",
                "");

        //then
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.NO_ID.getMessage())));
    }

    @Test
    public void updateFieldsForUser() throws Exception {
        //given
        User updatedUser = new User(null,
                "Taras",
                "Shevchenko",
                null,
                null,
                null);

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(USER)))
                .andExpect(status().isOk())
                .andDo(
                        result -> mvc.perform(patch("/users/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonConverter.toJson(updatedUser)))
                                .andExpect(jsonPath("$['data']['code']", Matchers.equalTo(200)))
                )
                .andDo(
                        result -> mvc.perform(get("/users")
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$['data']", hasSize(1)))
                                .andExpect(jsonPath("$['data'][0].firstName", equalTo("Taras")))
                                .andExpect(jsonPath("$['data'][0].lastName", equalTo("Shevchenko")))
                );
    }

    @Test
    public void updateAllFieldsForUserWithPatch() throws Exception { //is done to increase code coverage
        //given
        User updatedUser = new User("sergey.dn2003@gmail.com",
                "Taras",
                "Shevchenko",
                "2003-12-05",
                "",
                "");

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(USER)))
                .andExpect(status().isOk())
                .andDo(
                        result -> mvc.perform(patch("/users/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonConverter.toJson(updatedUser)))
                                .andExpect(jsonPath("$['data']['code']", Matchers.equalTo(200)))
                )
                .andDo(
                        result -> mvc.perform(get("/users")
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$['data']", hasSize(1)))
                                .andExpect(jsonPath("$['data'][0].firstName", equalTo("Taras")))
                                .andExpect(jsonPath("$['data'][0].lastName", equalTo("Shevchenko")))
                );
    }

    @Test
    public void updateFieldsForNotExistingUser() throws Exception {
        //given
        User updatedUser = new User(null,
                "Taras",
                "Shevchenko",
                null,
                null,
                null);

        //then
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.NO_ID.getMessage())));
    }

    @Test
    public void getUsersFromRange() throws Exception {
        //given
        User secondUser = new User("sergey.dn2003@gmail.com",
                "Lesya",
                "Ukrainka",
                "2001-12-05",
                "",
                "");
        User thirdUser = new User("sergey.dn2003@gmail.com",
                "Panteleimon",
                "Kulish",
                "1999-12-05",
                "",
                "");
        User fourthUser = new User("sergey.dn2003@gmail.com",
                "Ivan",
                "Franko",
                "1997-12-05",
                "",
                "");
        String from = "1998-12-05";
        String to = "2002-12-05";

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(USER)))
                .andExpect(status().isOk())
                .andDo(
                        result ->  mvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonConverter.toJson(secondUser)))
                                .andExpect(status().isOk())
                )
                .andDo(
                        result ->  mvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonConverter.toJson(thirdUser)))
                                .andExpect(status().isOk())
                )
                .andDo(
                        result ->  mvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonConverter.toJson(fourthUser)))
                                .andExpect(status().isOk())
                )
                .andDo(
                        result ->  mvc.perform(get("/users/range")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("from", from)
                                        .param("to", to))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$['data']", hasSize(2)))
                                .andExpect(jsonPath("$['data'][0].firstName", equalTo("Lesya")))
                                .andExpect(jsonPath("$['data'][1].firstName", equalTo("Panteleimon")))
                )
                .andDo(
                        result ->  mvc.perform(delete("/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                )
                .andDo(
                        result ->  mvc.perform(delete("/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                )
                .andDo(
                        result ->  mvc.perform(delete("/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                );
    }

    @Test
    public void getUsersFromRangeWhereFromIsInFuture() throws Exception {
        //given
        User secondUser = new User("sergey.dn2003@gmail.com",
                "Lesya",
                "Ukrainka",
                "2001-12-05",
                "",
                "");
        String from = "2025-12-05";
        String to = "2002-12-05";

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(USER)))
                .andExpect(status().isOk())
                .andDo(
                        result ->  mvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonConverter.toJson(secondUser)))
                                .andExpect(status().isOk())
                )
                .andDo(
                        result ->  mvc.perform(get("/users/range")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("from", from)
                                        .param("to", to))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.FUTURE_FROM_DATE.getMessage())))
                )
                .andDo(
                        result ->  mvc.perform(delete("/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                );
    }

    @Test
    public void getUsersFromRangeWhereToIsInFuture() throws Exception {
        //given
        User secondUser = new User("sergey.dn2003@gmail.com",
                "Lesya",
                "Ukrainka",
                "2001-12-05",
                "",
                "");
        String from = "1998-12-05";
        String to = "2025-12-05";

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(USER)))
                .andExpect(status().isOk())
                .andDo(
                        result ->  mvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonConverter.toJson(secondUser)))
                                .andExpect(status().isOk())
                )
                .andDo(
                        result ->  mvc.perform(get("/users/range")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("from", from)
                                        .param("to", to))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.FUTURE_TO_DATE.getMessage())))
                )
                .andDo(
                        result ->  mvc.perform(delete("/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                );
    }

    @Test
    public void getUsersFromRangeWhereFromIsMoreThanTo() throws Exception {
        //given
        User secondUser = new User("sergey.dn2003@gmail.com",
                "Lesya",
                "Ukrainka",
                "2001-12-05",
                "",
                "");
        String from = "2001-12-05";
        String to = "2000-12-05";

        //then
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConverter.toJson(USER)))
                .andExpect(status().isOk())
                .andDo(
                        result ->  mvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonConverter.toJson(secondUser)))
                                .andExpect(status().isOk())
                )
                .andDo(
                        result ->  mvc.perform(get("/users/range")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .param("from", from)
                                        .param("to", to))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$['message']", equalTo(ErrorMessage.FROM_MORE_THAN_TO.getMessage())))
                )
                .andDo(
                        result ->  mvc.perform(delete("/users/1")
                                        .contentType(MediaType.APPLICATION_JSON))
                );
    }

    private static Stream<User> invalidEmails(){
        return Stream.of(
                new User("sergey..dn2003@gmail.com",
                        "Serhii",
                        "Holiev",
                        "2003-12-05",
                        "Street 1",
                        "+4366499335880"),
                new User(".sergey.dn2003@gmail.com",
                        "Serhii",
                        "Holiev",
                        "2003-12-05",
                        "Street 1",
                        "+4366499335880"),
                new User("sergey.dn2003.@gmail.com",
                        "Serhii",
                        "Holiev",
                        "2003-12-05",
                        "Street 1",
                        "+4366499335880"),
                new User("sergey.dn2003@gmail..com",
                        "Serhii",
                        "Holiev",
                        "2003-12-05",
                        "Street 1",
                        "+4366499335880"),
                new User("sergey.dn2003@gmail.com.",
                        "Serhii",
                        "Holiev",
                        "2003-12-05",
                        "Street 1",
                        "+4366499335880"),
                new User("sergey.dn2003@-gmail.com",
                        "Serhii",
                        "Holiev",
                        "2003-12-05",
                        "Street 1",
                        "+4366499335880")
        );
    }
}