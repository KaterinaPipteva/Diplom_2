package practicum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class GetUserOrdersTest {
    BurgerClient burgerClient;
    User user;
    Order order;
    String token;

    @Before
    public void setUp(){
        burgerClient = new BurgerClient();
        user = UserGenerator.getRandomUser();
        String ingredient1 = burgerClient.getIngredients().extract().path("data[0]._id");
        String ingredient2 = burgerClient.getIngredients().extract().path("data[1]._id");
        order = new OrderGenerator().getOrderWithTwoIngredients(ingredient1, ingredient2);
    }

    @After
    public void tearDown(){
        if(token != null){
            burgerClient.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Get user order info with authorization token before creation order")
    @Description("Test for success get order info with authorization token before user create an order")
    public void getInfoOfUserOrderWithAuthorizationBeforeCreationOrder(){
        ValidatableResponse createUserResponse = burgerClient.createUser(user);
        int createUserStatusCode = createUserResponse.extract().statusCode();
        assertEquals(SC_OK, createUserStatusCode);
        token = createUserResponse.extract().path("accessToken");

        ValidatableResponse getOrderInfoBeforeCreatingOrderResponse = burgerClient.getOrderInfo(token);
        int getOrdersStatusCodeBeforeCreatingOrder = getOrderInfoBeforeCreatingOrderResponse.extract().statusCode();
        assertEquals(SC_OK, getOrdersStatusCodeBeforeCreatingOrder);
        String orderIdFromGetUserInfo = getOrderInfoBeforeCreatingOrderResponse.extract().path("orders[0]._id");
        assertNull(orderIdFromGetUserInfo);
        boolean isSuccess = getOrderInfoBeforeCreatingOrderResponse.extract().path("success");
        Assert.assertTrue(isSuccess);
    }

    @Test
    @DisplayName("Get user order info with authorization token after creation order")
    @Description("Test for success get order info with authorization token after user created an order")
    public void getInfoOfUserOrderWithAuthorizationAfterCreationOrder(){
        ValidatableResponse createUserResponse = burgerClient.createUser(user);
        int createUserStatusCode = createUserResponse.extract().statusCode();
        assertEquals(SC_OK, createUserStatusCode);
        token = createUserResponse.extract().path("accessToken");

        ValidatableResponse createOrderResponse = burgerClient.createOrder(order,token);
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        assertEquals(SC_OK, createOrderStatusCode);
        String orderId = createOrderResponse.extract().path("order._id");
        assertNotNull(orderId);

        ValidatableResponse getOrderInfoAfterCreatingOrderResponse = burgerClient.getOrderInfo(token);
        int getOrdersStatusCodeAfterCreatingOrder = getOrderInfoAfterCreatingOrderResponse.extract().statusCode();
        assertEquals(SC_OK, getOrdersStatusCodeAfterCreatingOrder);
        String orderIdOfUserOrder = getOrderInfoAfterCreatingOrderResponse.extract().path("orders[0]._id");
        assertEquals(orderId, orderIdOfUserOrder);
    }

    @Test
    @DisplayName("Get user order info without authorization token")
    @Description("Test for failure get order info of user without authorization token")
    public void getInfoOfUserOrderWithoutAuthorization(){
        token = "";
        ValidatableResponse getOrderInfoResponse = burgerClient.getOrderInfo(token);
        int getOrderStatusCode = getOrderInfoResponse.extract().statusCode();
        assertEquals(SC_UNAUTHORIZED, getOrderStatusCode);
        boolean isSuccess = getOrderInfoResponse.extract().path("success");
        Assert.assertFalse(isSuccess);
        String actualMessage = getOrderInfoResponse.extract().path("message");
        assertEquals("You should be authorised", actualMessage);
    }
}
