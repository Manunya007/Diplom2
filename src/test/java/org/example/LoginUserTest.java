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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

public class LoginUserTest extends BaseTest{

    private UserSteps userSteps = new UserSteps();
    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setEmail(RandomStringUtils.randomAlphabetic(12) + "@yandex.ru")
                .setPassword("rtuihjh")
                .setName("Username");
    }

    @DisplayName("Авторизация пользователя")
    @Test
    public void authorizationUserReturn200() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
       userSteps
               .createUser(user);
        userSteps
                .loginUser(user)
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @DisplayName("Авторизация пользователя с некорректным email")
    @Test
    public void authorizationUserIncorrectEmailReturn401() {
        User incorrectUser = new User()
                .setEmail(RandomStringUtils.randomAlphabetic(12))
                        .setPassword(user.getPassword())
                                .setName(user.getName());
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
        userSteps
                .createUser(user);

        userSteps
                .loginUser(incorrectUser)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false));
    }

    @DisplayName("Авторизация пользователя с некорректным паролем")
    @Test
    public void authorizationUserIncorrectPasswordReturn401() {
        User incorrectUser = new User()
                .setEmail(user.getEmail())
                .setPassword("fhfhf")
                .setName(user.getName());
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
        userSteps
                .createUser(user);
userSteps
        .loginUser(incorrectUser)

                .statusCode(SC_UNAUTHORIZED)
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
