package practicum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class LoginUserTest {
    BurgerClient burgerClient;
    User user;
    String token;
    UserCredentials userCredentials;

    @Before
    public void setUp(){
        burgerClient = new BurgerClient();
        user = UserGenerator.getRandomUser();
        userCredentials = UserCredentials.from(user);
    }

    @After
    public void tearDown(){
        if(token != null){
            burgerClient.deleteUser(token);}
    }

    @Test
    @DisplayName("Success login with new user")
    @Description("Success test for login new user with token, login and password")
    public void loginNewUser(){
        ValidatableResponse createResponse = burgerClient.createUser(user);
        int createStatusCode = createResponse.extract().statusCode();
        assertEquals(SC_OK, createStatusCode);

        ValidatableResponse loginResponse = burgerClient.loginUser(userCredentials);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, loginStatusCode);
        boolean isSuccess = loginResponse.extract().path("success");
        Assert.assertTrue(isSuccess);
        token = loginResponse.extract().path("accessToken");
    }

    @Test
    @DisplayName("Failure login with non-existent user")
    @Description("Test for failure login user that was deleted before")
    public void loginNonExistentUser(){
        ValidatableResponse createResponse = burgerClient.createUser(user);
        int createStatusCode = createResponse.extract().statusCode();
        assertEquals(SC_OK, createStatusCode);
        token = createResponse.extract().path("accessToken");

        ValidatableResponse deleteResponse = burgerClient.deleteUser(token);
        int deleteStatusCode = deleteResponse.extract().statusCode();
        assertEquals(SC_ACCEPTED, deleteStatusCode);
        boolean isSuccessDelete = deleteResponse.extract().path("success");
        Assert.assertTrue(isSuccessDelete);

        ValidatableResponse loginResponse = burgerClient.loginUser(userCredentials);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(SC_UNAUTHORIZED, loginStatusCode);
        boolean isSuccessLogin = loginResponse.extract().path("success");
        Assert.assertFalse(isSuccessLogin);

    }
}
