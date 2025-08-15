package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.Orders;
import org.example.endpoints.Endpoints;

import static io.restassured.RestAssured.given;

public class OrdersSteps {

    @Step("Создание заказа")
    public ValidatableResponse createOrders(Orders orders) {
       return given()
                .body(orders)
                .when()
                .post(Endpoints.CREATE_ORDERS)
                .then();
    }
}
