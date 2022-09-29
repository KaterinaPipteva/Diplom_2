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
import static org.junit.Assert.assertEquals;

public class ChangeUserTest {
    BurgerClient burgerClient;
    User firstUser;
    User secondUser;
    String token;
    UserCredentials firstUserCredentials;
    UserCredentials secondUserCredentials;

    @Before
    public void setUp(){
        burgerClient = new BurgerClient();
        firstUser = UserGenerator.getRandomUser();
        firstUserCredentials = UserCredentials.from(firstUser);
        secondUser = UserGenerator.getRandomUser();
        secondUserCredentials = UserCredentials.from(secondUser);
    }

    @After
    public void tearDown(){
        if(token != null){
            burgerClient.deleteUser(token); }
    }

    @Test
    @DisplayName("Change user with authorization token")
    @Description("test for success changing user data (email, name, password) with authorization token")
    public void changeUserWithAuthorizationTest(){
        ValidatableResponse createResponse = burgerClient.createUser(firstUser);
        int createStatusCode = createResponse.extract().statusCode();
        assertEquals(SC_OK, createStatusCode);
        token = createResponse.extract().path("accessToken");

        ValidatableResponse firstLoginResponse = burgerClient.loginUser(firstUserCredentials);
        int firstLoginStatusCode = firstLoginResponse.extract().statusCode();
        assertEquals(SC_OK, firstLoginStatusCode);

        ValidatableResponse changeResponse = burgerClient.changeUserData(secondUser, token);
        int changeStatusCode = changeResponse.extract().statusCode();
        assertEquals(SC_OK, changeStatusCode);
        boolean isChangeSuccess = changeResponse.extract().path("success");
        Assert.assertTrue(isChangeSuccess);

        ValidatableResponse secondLoginResponse = burgerClient.loginUser(secondUserCredentials);
        int secondLoginStatusCode = secondLoginResponse.extract().statusCode();
        assertEquals(SC_OK, secondLoginStatusCode);
    }

    @Test
    @DisplayName("Change user without authorization token")
    @Description("Test for failure changing user data (email, name, password) without authorization token")
    public void changeUserWithoutAuthorizationTest(){
        ValidatableResponse createResponse = burgerClient.createUser(firstUser);
        int createStatusCode = createResponse.extract().statusCode();
        assertEquals(SC_OK, createStatusCode);
        token = "";

        ValidatableResponse changeResponse = burgerClient.changeUserData(secondUser, token);
        int changeStatusCode = changeResponse.extract().statusCode();
        assertEquals(SC_UNAUTHORIZED, changeStatusCode);
        boolean isChangeSuccess = changeResponse.extract().path("success");
        Assert.assertFalse(isChangeSuccess);
        String actualError = changeResponse.extract().path("message");
        assertEquals("You should be authorised", actualError);

        ValidatableResponse firstLoginResponse = burgerClient.loginUser(firstUserCredentials);
        int firstLoginStatusCode = firstLoginResponse.extract().statusCode();
        assertEquals(SC_OK, firstLoginStatusCode);

        token = createResponse.extract().path("accessToken");
    }

}
