package practicum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Order {
    private List<String> ingredients = new ArrayList<>();

    public void addIngredients(String ingredient){
        ingredients.add(ingredient);
    }
}
