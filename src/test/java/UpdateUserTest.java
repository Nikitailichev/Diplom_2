import api.*;
import generators.UserGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class UpdateUserTest {
    private UserClient userClient;
    private User user;
    private LoginUser login;
    private String token;
    private String bearerToken;
    private String newEmail;
    private String newPassword;
    private String newName;
    private final static String ERROR_MESSAGE_NOT_AUTHORISED = "You should be authorised";

    @Before
    public void beforeCreateUserTest(){
        userClient = new UserClient();
        user = UserGenerator.getSuccessCreateUser();
        login = new LoginUser();
        newEmail = UserGenerator.getNewEmail();
        newPassword = UserGenerator.getNewPassword();
        newName = UserGenerator.getNewName();

        ValidatableResponse responseCreate = userClient.createUserRequest(user);
        bearerToken = responseCreate.extract().path("accessToken");
        token = bearerToken.substring(7);

        userClient.loginUserRequest(LoginUser.from(user));
    }

    @After
    public void deleteUser() {
        if(token != null){
            userClient.deleteUserRequest(token);
        }
    }

    @Test
    @DisplayName("Check to update a user with login (Email)")
    @Description("Изменение данных пользователя с авторизацией (Email)")
    public void updateUserEmailWithLoginTest(){
        user.setEmail(newEmail);
        ValidatableResponse responseUpdate = userClient.updateUserRequest(user, token);
        int actualStatusCode = responseUpdate.extract().statusCode();
        Boolean isUserUpdated = responseUpdate.extract().path("success");
        String actualResponce = (responseUpdate.extract().path("user")).toString();
        boolean isEmailUpdated = actualResponce.contains(newEmail);
        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("User is not login", isUserUpdated);
        assertTrue("Email is not new", isEmailUpdated);
    }

    @Test
    @DisplayName("Check to update a user with login (Name)")
    @Description("Изменение данных пользователя с авторизацией (Name)")
    public void updateUserNameWithLoginTest(){
        user.setName(newName);
        ValidatableResponse responseUpdate = userClient.updateUserRequest(user, token);
        int actualStatusCode = responseUpdate.extract().statusCode();
        Boolean isUserUpdated = responseUpdate.extract().path("success");
        String actualResponce = (responseUpdate.extract().path("user")).toString();
        boolean isNameUpdated = actualResponce.contains(newName);
        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("User is not login", isUserUpdated);
        assertTrue("Name is not new", isNameUpdated);
    }

    @Test
    @DisplayName("Check to update a user with login (Password)")
    @Description("Изменение данных пользователя с авторизацией (Password)")
    public void updateUserPasswordWithLoginTest(){
        user.setPassword(newPassword);
        ValidatableResponse responseUpdate = userClient.updateUserRequest(user, token);
        int actualStatusCode = responseUpdate.extract().statusCode();
        Boolean isUserUpdated = responseUpdate.extract().path("success");
        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("User is not login", isUserUpdated);

        ValidatableResponse responseSecondLogin = userClient.loginUserRequest(LoginUser.from(user));
        Boolean isUserSecondlogged = responseSecondLogin.extract().path("success");
        assertTrue("User is not login", isUserSecondlogged);
    }

    @Test
    @DisplayName("Check to update a user without login (Email)")
    @Description("Изменение данных пользователя без авторизации (Email)")
    public void updateUserEmailWithoutLoginTest(){
        user.setEmail(newEmail);
        ValidatableResponse responseUpdate = userClient.updateUserRequest(user, "");
        int actualStatusCode = responseUpdate.extract().statusCode();
        String actualMessage = responseUpdate.extract().path("message");
        assertEquals("StatusCode is not 403", SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_NOT_AUTHORISED, actualMessage);
    }

    @Test
    @DisplayName("Check to update a user without login (Name)")
    @Description("Изменение данных пользователя без авторизации (Name)")
    public void updateUserNameWithoutLoginTest(){
        user.setName(newName);
        ValidatableResponse responseUpdate = userClient.updateUserRequest(user, "");
        int actualStatusCode = responseUpdate.extract().statusCode();
        String actualMessage = responseUpdate.extract().path("message");
        assertEquals("StatusCode is not 403", SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_NOT_AUTHORISED, actualMessage);
    }

    @Test
    @DisplayName("Check to update a user without login (Password)")
    @Description("Изменение данных пользователя без авторизации (Password)")
    public void updateUserPasswordWithoutLoginTest(){
        user.setPassword(newPassword);
        ValidatableResponse responseUpdate = userClient.updateUserRequest(user, "");
        int actualStatusCode = responseUpdate.extract().statusCode();
        String actualMessage = responseUpdate.extract().path("message");
        assertEquals("StatusCode is not 403", SC_UNAUTHORIZED, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_NOT_AUTHORISED, actualMessage);
    }
}