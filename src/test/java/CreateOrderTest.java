import api.*;
import data.*;
import generators.UserGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateOrderTest {
    private UserClient userClient;
    private User user;
    private LoginUser login;
    private IngredientClient getIngredients;
    private OrderClient orderClient;
    private String token;
    private String bearerToken;

    @Before
    public void beforeCreateUserTest(){
        userClient = new UserClient();
        user = UserGenerator.getSuccessCreateUser();
        login = new LoginUser();

        getIngredients = new IngredientClient();
        orderClient = new OrderClient();

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
    @DisplayName("Check to create order with ingredients without login")
    @Description("Создание заказа с ингредиентами без авторизации")
    public void createOrderWithoutLoginTest() {
        ValidatableResponse responseIngredients = getIngredients.getIngredientsRequest();
        Ingredient ingredient = responseIngredients.extract().body().as(Ingredient.class);
        ArrayList<Ingredient> list = ingredient.getData();
        ArrayList<String> listIngredients = new ArrayList<>();
        int max = list.size();
        for (int i = 0; i < max; i++)
        {
            listIngredients.add(list.get(i).get_id());
        }
        ListIngredient listIngredient = new ListIngredient(listIngredients);

        ValidatableResponse responseCreateOrder = orderClient.createOrderRequest(listIngredient,"");
        int actualStatusCode = responseCreateOrder.extract().statusCode();
        Boolean isOrderCreated = responseCreateOrder.extract().path("success");
        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("Order is not created", isOrderCreated);
    }

    @Test
    @DisplayName("Check to create order with ingredients with login")
    @Description("Создание заказа с ингредиентами с авторизацией")
    public void createOrderWithLoginTest() {
        ValidatableResponse responseIngredients = getIngredients.getIngredientsRequest();
        Ingredient ingredient = responseIngredients.extract().body().as(Ingredient.class);
        ArrayList<Ingredient> list = ingredient.getData();
        ArrayList<String> listIngredients = new ArrayList<>();
        int max = list.size();
        for (int i = 0; i < max; i++)
        {
            listIngredients.add(list.get(i).get_id());
        }
        ListIngredient listIngredient = new ListIngredient(listIngredients);

        ValidatableResponse responseCreateOrder = orderClient.createOrderRequest(listIngredient,token);
        int actualStatusCode = responseCreateOrder.extract().statusCode();
        Boolean isOrderCreated = responseCreateOrder.extract().path("success");
        assertEquals("StatusCode is not 200", SC_OK, actualStatusCode);
        assertTrue("Order is not created", isOrderCreated);
    }

    @Test
    @DisplayName("Check to create order without ingredients")
    @Description("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        ArrayList<String> listString = new ArrayList<>();
        ListIngredient listIngredient = new ListIngredient(listString);

        ValidatableResponse responseCreateOrder = orderClient.createOrderRequest(listIngredient,token);
        int actualStatusCode = responseCreateOrder.extract().statusCode();
        Boolean isOrderCreated = responseCreateOrder.extract().path("success");
        assertEquals("StatusCode is not 400", SC_BAD_REQUEST, actualStatusCode);
        assertFalse("Order is created", isOrderCreated);
    }

    @Test
    @DisplayName("Check to create order with incorrect hash of ingredients")
    @Description("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithIncorrectHashTest() {
        ValidatableResponse responseIngredients = getIngredients.getIngredientsRequest();
        Ingredient ingredient = responseIngredients.extract().body().as(Ingredient.class);
        ArrayList<Ingredient> list = ingredient.getData();
        ArrayList<String> listIngredients = new ArrayList<>();
        int max = list.size();
        Random random = new Random();
        for (int i = 0; i < max; i++)
        {
            listIngredients.add(list.get(i).get_id()+random.nextInt(50));
        }
        ListIngredient listIngredient = new ListIngredient(listIngredients);

        ValidatableResponse responseCreateOrder = orderClient.createOrderRequest(listIngredient,token);
        int actualStatusCode = responseCreateOrder.extract().statusCode();
        assertEquals("StatusCode is not 500", SC_INTERNAL_SERVER_ERROR, actualStatusCode);
    }
}