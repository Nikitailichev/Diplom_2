import api.*;
import generators.UserGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateUserTest {
    private UserClient userClient;
    private User user;
    private LoginUser login;
    private String token;
    private String bearerToken;
    private final static String ERROR_MESSAGE_NOT_UNIQUE = "User already exists";
    private final static String ERROR_MESSAGE_REQUIRED_FIELD = "Email, password and name are required fields";

    @Before
    public void beforeCreateUserTest(){
        userClient = new UserClient();
        user = UserGenerator.getSuccessCreateUser();
        login = new LoginUser();
    }

    @After
    public void deleteUser() {
        if(token != null){
            userClient.deleteUserRequest(token);
        }
    }

    @Test
    @DisplayName("Check to create a unique user")
    @Description("создать уникального пользователя")
    public void createUniqueUserTest(){
        ValidatableResponse responseCreate = userClient.createUserRequest(user);
        int actualStatusCode = responseCreate.extract().statusCode();
        Boolean isUserCreated = responseCreate.extract().path("success");
        bearerToken = responseCreate.extract().path("accessToken");
        token = bearerToken.substring(7);
        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("User is not created", isUserCreated);

        ValidatableResponse responseLogin = userClient.loginUserRequest(LoginUser.from(user));
        Boolean isUserlogged = responseLogin.extract().path("success");
        assertTrue("User is not login", isUserlogged);
    }

    @Test
    @DisplayName("Check to create a not unique user")
    @Description("создать пользователя, который уже зарегистрирован")
    public void createNotUniqueUserTest(){
        ValidatableResponse responseCreate = userClient.createUserRequest(user);
        Boolean isUserCreated = responseCreate.extract().path("success");
        bearerToken = responseCreate.extract().path("accessToken");
        token = bearerToken.substring(7);
        assertTrue("User is not created", isUserCreated);

        ValidatableResponse responseLogin = userClient.loginUserRequest(LoginUser.from(user));
        Boolean isUserlogged = responseLogin.extract().path("success");
        assertTrue("User is not login", isUserlogged);

        responseCreate = userClient.createUserRequest(user);
        int actualStatusCode = responseCreate.extract().statusCode();
        String actualMessage = responseCreate.extract().path("message");
        assertEquals("StatusCode is not 403", SC_FORBIDDEN, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_NOT_UNIQUE, actualMessage);
    }

    @Test
    @DisplayName("Check to create a user without some required field - email")
    @Description("создать пользователя и не заполнить одно из обязательных полей (email)")
    public void createUserWithoutEmailTest(){
        user.setEmail(null);
        ValidatableResponse responseCreate = userClient.createUserRequest(user);
        int actualStatusCode = responseCreate.extract().statusCode();
        String actualMessage = responseCreate.extract().path("message");
        assertEquals("StatusCode is not 403", SC_FORBIDDEN, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_REQUIRED_FIELD, actualMessage);
    }

    @Test
    @DisplayName("Check to create a user without some required field - password")
    @Description("создать пользователя и не заполнить одно из обязательных полей (password)")
    public void createUserWithoutPasswordTest(){
        user.setPassword(null);
        ValidatableResponse responseCreate = userClient.createUserRequest(user);
        int actualStatusCode = responseCreate.extract().statusCode();
        String actualMessage = responseCreate.extract().path("message");
        assertEquals("StatusCode is not 403", SC_FORBIDDEN, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_REQUIRED_FIELD, actualMessage);
    }

    @Test
    @DisplayName("Check to create a user without some required field - name")
    @Description("создать пользователя и не заполнить одно из обязательных полей (name)")
    public void createUserWithoutNameTest(){
        user.setName(null);
        ValidatableResponse responseCreate = userClient.createUserRequest(user);
        int actualStatusCode = responseCreate.extract().statusCode();
        String actualMessage = responseCreate.extract().path("message");
        assertEquals("StatusCode is not 403", SC_FORBIDDEN, actualStatusCode);
        assertEquals("Message is not correct", ERROR_MESSAGE_REQUIRED_FIELD, actualMessage);
    }
}