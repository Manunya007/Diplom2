package org.example;

import io.qameta.allure.Description;
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
@Description("Успешная регистрация пользователя")
    @Test
    public void createUserReturn200Test() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
userSteps
        .createUser(user)
        .statusCode(SC_OK)
                .body("success", is(true));
    }

    @DisplayName("Регистрация дубликата пользователя")
    @Description("Появление ошибки 403 при регистрации уже существующего пользователя")
    @Test
    public void createDuplicateUserReturn403Test() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
      userSteps
                .createUser(user);

      userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
              .body("message", is( "User already exists"));
    }


    @DisplayName("Регистрация пользователя без email")
    @Description("Появление ошибки 403 при регистрации пользователя с незаполненным email")
    @Test
    public void createUserEmailNullReturn403Test() {
        User userWithoutEmail = new User()
                .setPassword("rtuihjh")
                .setName("Username");
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
     userSteps
              .createUser(userWithoutEmail)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
             .body("message", is("Email, password and name are required fields"));
    }

    @DisplayName("Регистрация пользователя без пароля")
    @Description("Появление ошибки 403 при регистрации пользователя с незаполненным паролем")
    @Test
    public void createUserPasswordNullReturn403Test() {
        User userWithoutPassword = new User()
                .setEmail(RandomStringUtils.randomAlphabetic(12) + "@yandex.ru")
                .setName("Username");
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
        userSteps
                .createUser(userWithoutPassword)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @DisplayName("Регистрация пользователя без имени")
    @Description("Появление ошибки 403 при регистрации пользователя с незаполненным именем")
    @Test
    public void createUserUsernameNullReturn403Test() {
        User userWithoutName = new User()
                .setEmail(RandomStringUtils.randomAlphabetic(12) + "@yandex.ru")
                .setPassword("rtuihjh");
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
        userSteps
                .createUser(userWithoutName)
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
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