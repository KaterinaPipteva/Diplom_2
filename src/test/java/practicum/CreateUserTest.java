package practicum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;

public class CreateUserTest {
    BurgerClient burgerClient;
    User properUser;
    User improperUser;
    String token;

    @Before
    public void setUp(){
        burgerClient = new BurgerClient();
        properUser = UserGenerator.getRandomUser();
        improperUser = UserGenerator.getUserWithoutEmail();
    }

    @After
    public void tearDown(){
        if(token != null){
        burgerClient.deleteUser(token); }
    }

    @Test
    @DisplayName("Create one unique user")
    @Description("Test for creation one user with unique data (name, email, password)")
    public void createUniqueUserTest(){
        ValidatableResponse createResponse = burgerClient.createUser(properUser);
        int statusCode = createResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);
        boolean isSuccess = createResponse.extract().path("success");
        Assert.assertTrue(isSuccess);

        token = createResponse.extract().path("accessToken");
    }

    @Test
    @DisplayName("Create two not unique users")
    @Description("Test for creation two user with the same data (name, email, password)")
    public void createTwoEqualUsersTest(){
        ValidatableResponse createFirstUserResponse = burgerClient.createUser(properUser);
        int firstStatusCode = createFirstUserResponse.extract().statusCode();
        assertEquals(SC_OK, firstStatusCode);
        boolean isSuccessFirst = createFirstUserResponse.extract().path("success");
        Assert.assertTrue(isSuccessFirst);
        token = createFirstUserResponse.extract().path("accessToken");

        ValidatableResponse createSecondUserResponse = burgerClient.createUser(properUser);
        int secondStatusCode = createSecondUserResponse.extract().statusCode();
        assertEquals(SC_FORBIDDEN, secondStatusCode);
        boolean isSuccessSecond = createSecondUserResponse.extract().path("success");
        Assert.assertFalse(isSuccessSecond);
    }

    @Test
    @DisplayName("Create user without email")
    @Description("Test for creation user without email but with name and password)")
    public void createUserWithoutEmail(){
        ValidatableResponse createResponse = burgerClient.createUser(improperUser);
        int statusCode = createResponse.extract().statusCode();
        assertEquals(SC_FORBIDDEN, statusCode);
        boolean isSuccess = createResponse.extract().path("success");
        Assert.assertFalse(isSuccess);
    }
}
