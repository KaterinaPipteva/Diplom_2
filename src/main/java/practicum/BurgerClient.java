package practicum;

import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class BurgerClient extends RestClient {
    private static final String USER_CREATE_PATH = "api/auth/register";
    private static final String USER_LOGIN_PATH = "api/auth/login";
    private static final String USER_INFO_PATH = "api/auth/user";
    private static final String ORDER_INFO_PATH = "api/orders";
    private static final String INGREDIENTS_PATH = "api/ingredients";


    public ValidatableResponse createUser(User user){
        return given()
                .spec(getBaseSpec())
                .log().all()
                .body(user)
                .when()
                .post(USER_CREATE_PATH)
                .then()
                .log().all();
    }

    public ValidatableResponse loginUser(UserCredentials userCredentials){
        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .when()
                .post(USER_LOGIN_PATH)
                .then();
    }

    public ValidatableResponse changeUserData(User user, String token){
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(USER_INFO_PATH)
                .then()
                .log().all();
    }

    public ValidatableResponse getIngredients(){
        return given()
                .spec(getBaseSpec())
                .when()
                .get(INGREDIENTS_PATH)
                .then();
    }

    public ValidatableResponse createOrder(Order order, String token){
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .body(order)
                .when()
                .post(ORDER_INFO_PATH)
                .then();

    }

public ValidatableResponse getOrderInfo(String token){
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when()
                .get(ORDER_INFO_PATH)
                .then();
}

public ValidatableResponse deleteUser(String token){
        return given()
                .spec(getBaseSpec())
                .log().all()
                .header("Authorization", token)
                .when()
                .delete(USER_INFO_PATH)
                .then()
                .log().all();
}

}
