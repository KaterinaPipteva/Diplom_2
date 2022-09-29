package practicum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class CreateOrderTest {
    BurgerClient burgerClient;
    User user;
    Order order;
    Order emptyOrder;
    Order fakeOrder;
    String ingredient1;
    String ingredient2;
    String token;

    @Before
    public void setUp(){
        burgerClient = new BurgerClient();
        user = UserGenerator.getRandomUser();

        //Коммент для ревьюира: можно было по другому сделать: через десериализацию прогнать запрос
        // про ингредиенты и уже от туда вытаскивать id ингредиентов, но ради одного теста
        // не хотелось так много времени тратить с учетом того что не успеваю со сдачей дипломного проекта,
        // но понимаю что решение с десериализацией было бы лучше
        ingredient1 = burgerClient.getIngredients().extract().path("data[0]._id");
        ingredient2 = burgerClient.getIngredients().extract().path("data[1]._id");
        order = new OrderGenerator().getOrderWithTwoIngredients(ingredient1, ingredient2);
        emptyOrder = new OrderGenerator().getOrderWithoutIngredients();
        fakeOrder = new OrderGenerator().getOrderWithFakeIngredients();
    }

    @After
    public void tearDown(){
        if(token != null){
            burgerClient.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Create order with authorization token and ingredients")
    @Description("Test for success creation of order with authorization token and ingredients")
    public void createOrderWithAuthorizationAndIngredientsTest(){
        ValidatableResponse createUserResponse = burgerClient.createUser(user);
        int createUserStatusCode = createUserResponse.extract().statusCode();
        assertEquals(SC_OK, createUserStatusCode);
        token = createUserResponse.extract().path("accessToken");

        ValidatableResponse createOrderResponse = burgerClient.createOrder(order, token);
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        assertEquals(SC_OK, createOrderStatusCode);
        boolean isSuccess = createOrderResponse.extract().path("success");
        Assert.assertTrue(isSuccess);

        List<HashMap<String, String>> ingredientsParameter = createOrderResponse.extract().path("order.ingredients");
        Assert.assertNotNull(ingredientsParameter);
        String idParameter = createOrderResponse.extract().path("order._id");
        Assert.assertNotNull(idParameter);
        HashMap<String, String> ownerParameter = createOrderResponse.extract().path("order.owner");
        Assert.assertNotNull(ownerParameter);

    }

    @Test
    @DisplayName("Create order without authorization token but with ingredients")
    @Description("Test for creation of order without authorization token but with ingredients")
    public void createOrderWithoutAuthorization(){
        token = "";
        ValidatableResponse createOrderResponse = burgerClient.createOrder(order, token);
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        assertEquals(SC_OK, createOrderStatusCode);

        List<HashMap<String, String>> ingredientsParameter = createOrderResponse.extract().path("order.ingredients");
        Assert.assertNull(ingredientsParameter);

        String idParameter = createOrderResponse.extract().path("order._id");
        Assert.assertNull(idParameter);

        HashMap<String, String> ownerParameter = createOrderResponse.extract().path("order.owner");
        Assert.assertNull(ownerParameter);
        token = null;
    }

    @Test
    @DisplayName("Create order without ingredients")
    @Description("Test for failure creation order without ingredients and authorization token")
    public void createOrderWithoutIngredients(){
        token = "";

        ValidatableResponse createOrderResponse = burgerClient.createOrder(emptyOrder, token);
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, createOrderStatusCode);
        boolean isSuccess = createOrderResponse.extract().path("success");
        Assert.assertFalse(isSuccess);
        String message = createOrderResponse.extract().path("message");
        assertEquals("Ingredient ids must be provided", message);
        token = null;
    }

    @Test
    @DisplayName("Create order with fake ingredients")
    @Description("Test for failure creation order with fake ingredients and without authorization token")
    public void createOrderWithFakeIngredients(){
        token = "";

        ValidatableResponse createOrderResponse = burgerClient.createOrder(fakeOrder, token);
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, createOrderStatusCode);
        boolean isSuccess = createOrderResponse.extract().path("success");
        Assert.assertFalse(isSuccess);
        String message = createOrderResponse.extract().path("message");
        assertEquals("One or more ids provided are incorrect", message);
        token = null;
    }
}
