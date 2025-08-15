package org.example;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import org.example.endpoints.Endpoints;
import org.junit.Before;

public class BaseTest {
    @Before
    public void starUp(){
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(Endpoints.HOST)
                .setContentType(ContentType.JSON)
                .build();
        RestAssured.config = RestAssured
                .config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }
}
