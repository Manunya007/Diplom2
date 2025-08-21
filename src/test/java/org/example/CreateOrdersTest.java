package org.example;

import io.qameta.allure.Description;
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
        new UserSteps()
                .createUser(user);
        new UserSteps()
                .loginUser(user);
        orders = new Orders();
        List<String> ingredientsList = Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f");
        orders.setIngredients(ingredientsList);
    }

    @DisplayName("Успешное создание заказа")
    @Description("Создание заказа с ингредиентами после авторизации зарегистрированного пользователя")
    @Test
    public void createOrdersReturn200Test() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
ordersSteps
        .createOrders(orders)
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @DisplayName("Создание заказа без авторизации")
    @Description("Появление ошибки 403 при создании заказа с ингредиентами без авторизации")
    @Test
   public void createOrdersNoAuthorizationReturn403Test() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
        ValidatableResponse response = userSteps.loginUser(user);
            String token = response.extract().path("accessToken");
                userSteps.deleteUser(token);
       ordersSteps
               .createOrders(orders)
                .statusCode(SC_FORBIDDEN)
               .body("success", is(false))
               .body("message", is("You should be authorised"));
    }

    @DisplayName("Создание заказа без ингредиентов")
    @Description("Появление ошибки 400 при создании заказа без ингредиентов после авторизации зарегистрированного пользователя")
    @Test
    public void createOrdersNoIngredientsReturn400Test() {
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
        List<String> noIngredientsList = List.of();
        Orders ordersWithoutIngredients = new Orders()
                .setIngredients(noIngredientsList);
       ordersSteps
               .createOrders(ordersWithoutIngredients)
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
               .body("message", is("Ingredient ids must be provided"));
    }

    @DisplayName("Создание заказа неверным хэшем ингредиентов")
    @Description("Появление ошибки 500 при создании заказа с неверным хэшем ингредиентов после авторизации зарегистрированного пользователя")
    @Test
    public void createOrdersIncorrectHashIngredientsReturn500Test() {
        List<String> incorrectIngredientsList = Arrays.asList(
                "61c0c1bdaaa6d",
                "61c0c5aaaa6f");
        Orders ordersIncorrectIngredients = new Orders()
                .setIngredients(incorrectIngredientsList);
        RestAssured.filters(new RequestLoggingFilter(), new RequestLoggingFilter());
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
