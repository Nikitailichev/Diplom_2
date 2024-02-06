package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static config.Enviroment.BASE_URL;
import static io.restassured.RestAssured.given;

public class IngredientClient {
    private static final String PATH_INGREDIENTS = "/api/ingredients";

    @Step("Get ingredients")
    public ValidatableResponse getIngredientsRequest() {
        return given()
                .when()
                .get(BASE_URL + PATH_INGREDIENTS)
                .then();
    }
}