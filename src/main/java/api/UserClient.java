package api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static config.Enviroment.BASE_URL;
import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String PATH_CREATE = "/api/auth/register";
    private static final String PATH_LOGIN = "/api/auth/login";
    private static final String PATH_USER = "/api/auth/user";

    @Step("Create user")
    public ValidatableResponse createUserRequest(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(BASE_URL + PATH_CREATE)
                .then();
    }

    @Step("Login user")
    public ValidatableResponse loginUserRequest(LoginUser loginUser) {
        return given()
                .contentType(ContentType.JSON)
                .body(loginUser)
                .when()
                .post(BASE_URL + PATH_LOGIN)
                .then();
    }

    @Step("Update user")
    public ValidatableResponse updateUserRequest(User user, String token) {
        return given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .patch(BASE_URL + PATH_USER)
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUserRequest(String token) {
        return given()
                .auth().oauth2(token)
                .when()
                .delete(BASE_URL + PATH_USER)
                .then();
    }
}