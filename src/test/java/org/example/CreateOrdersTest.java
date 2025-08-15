package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.steps.OrdersSteps;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;

public class CreateOrdersTest extends BaseTest{
    private OrdersSteps ordersSteps = new OrdersSteps();
    private UserSteps userSteps = new UserSteps();
    private User user;
    private Orders orders;

    @Before
    public void setUp() {
        user = new User();
        user.setEmail(RandomStringUtils.randomAlphabetic(12) + "@yandex.ru")
                .setPassword("rtuihjh")
                .setName("Username");
        orders = new Orders();
        List<String> ingredientsList = Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f");
        orders.setIngredients(ingredientsList);
    }

    @DisplayName("Создание заказа после авторизации с ингредиентами")
    @Test
    public void createOrdersReturn200() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
       userSteps
               .createUser(user);
        userSteps
                .loginUser(user);
ordersSteps
        .createOrders(orders)
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @DisplayName("Создание заказа без авторизации")
    @Test
   public void createOrdersNoAuthorizationReturn403() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());

       ordersSteps
               .createOrders(orders)
                .statusCode(SC_FORBIDDEN)
               .body("success", is(false));
    }

    @DisplayName("Создание заказа после авторизации без ингредиентов")
    @Test
    public void createOrdersNoIngredientsReturn400() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
        List<String> noIngredientsList = List.of();
        Orders ordersWithoutIngredients = new Orders()
                .setIngredients(noIngredientsList);
        userSteps
                .createUser(user);
        userSteps
                .loginUser(user);
       ordersSteps
               .createOrders(ordersWithoutIngredients)
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false));
    }

    @DisplayName("Создание заказа после авторизации с неверным хэшем ингредиентов")
    @Test
    public void createOrdersIncorrectHashIngredientsReturn500() {
        List<String> incorrectIngredientsList = Arrays.asList(
                "61c0c1bdaaa6d",
                "61c0c5aaaa6f");
        Orders ordersIncorrectIngredients = new Orders()
                .setIngredients(incorrectIngredientsList);
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
      userSteps
              .createUser(user);
        userSteps
                .loginUser(user);
       ordersSteps
               .createOrders(ordersIncorrectIngredients)
                .statusCode(SC_INTERNAL_SERVER_ERROR);
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
            } catch (Exception ignored) {
            }
        }
    }
}
