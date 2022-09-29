package practicum;

import java.util.ArrayList;
import java.util.List;

public class OrderGenerator {
    public Order getOrderWithTwoIngredients(String ingredient1, String ingredient2){
        List<String> listOfIngredients = new ArrayList<>();
        listOfIngredients.add(ingredient1);
        listOfIngredients.add(ingredient2);
        return new Order(listOfIngredients);
    }

    public Order getOrderWithoutIngredients(){
        return new Order();
    }

    public Order getOrderWithFakeIngredients(){
        List<String> listOfIngredients = new ArrayList<>();
        listOfIngredients.add("61c0c5a71d1f70001bdaaa90");
        listOfIngredients.add("70c0c5a90d1f82001bdaaa6d");
        return new Order(listOfIngredients);
    }
}
