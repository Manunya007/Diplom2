package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;

public class CreateUserTest extends BaseTest{
    private UserSteps userSteps = new UserSteps();
    private User user;

@Before
public void setUp() {
    user = new User();
    user.setEmail(RandomStringUtils.randomAlphabetic(12) + "@yandex.ru")
            .setPassword("rtuihjh")
            .setName("Username");
}

@DisplayName("Регистрация пользователя")
    @Test
    public void createUserReturn200() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
userSteps
        .createUser(user)
        .statusCode(SC_OK)
                .body("success", is(true));
    }

    @DisplayName("Регистрация пользователя, который уже существует")
    @Test
    public void createDuplicateUserReturn403() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
      userSteps
                .createUser(user);

      userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false));
    }

    @DisplayName("Регистрация пользователя без email")
    @Test
    public void createUserEmailNullReturn403() {
        User userWithoutEmail = new User()
                .setPassword("rtuihjh")
                .setName("Username");
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
     userSteps
              .createUser(userWithoutEmail)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false));
    }

    @DisplayName("Регистрация пользователя без пароля")
    @Test
    public void createUserPasswordNullReturn403() {
        User userWithoutPassword = new User()
                .setEmail(RandomStringUtils.randomAlphabetic(12) + "@yandex.ru")
                .setName("Username");
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
        userSteps
                .createUser(userWithoutPassword)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false));
    }

    @DisplayName("Регистрация пользователя без имени")
    @Test
    public void createUserUsernameNullReturn403() {
        User userWithoutName = new User()
                .setEmail(RandomStringUtils.randomAlphabetic(12) + "@yandex.ru")
                .setPassword("rtuihjh");
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
        userSteps
                .createUser(userWithoutName)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false));

    }
    @After
    public void tearDown() {
        if (user != null) {
            try {
                ValidatableResponse response = userSteps.loginUser(user);
                if (response.extract().statusCode() == 200) {
                    String token = response.extract().path("accessToken");
                    if (token != null) {
                        userSteps.deleteUser(token);
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}