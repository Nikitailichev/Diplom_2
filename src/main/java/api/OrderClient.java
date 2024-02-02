package api;

import data.ListIngredient;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static config.Enviroment.BASE_URL;
import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String PATH_ORDERS = "/api/orders";

    @Step("Create order")
    public ValidatableResponse createOrderRequest(ListIngredient listIngredient, String token) {
        return given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(listIngredient)
                .when()
                .post(BASE_URL + PATH_ORDERS)
                .then();
    }

    @Step("Get orders by user")
    public ValidatableResponse getOrdersByUserRequest(String token) {
        return given()
                .auth().oauth2(token)
                .when()
                .get(BASE_URL + PATH_ORDERS)
                .then();
    }
}