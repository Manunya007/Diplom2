package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.User;
import org.example.endpoints.Endpoints;

import static io.restassured.RestAssured.given;

public class UserSteps {

    @Step("Регистрация пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .body(user)
                .when()
                .post(Endpoints.CREATE_USER)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser (User user){
        return given()
                .body(user)
                .when()
                .post(Endpoints.LOGIN_USER)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String token){
       return given()
               .header("Authorization", token)
                .when()
                .delete(Endpoints.DELETE_USER)
                .then();

    }
}