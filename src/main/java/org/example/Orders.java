package org.example;

import java.util.List;

public class Orders {
    private List<String> ingredients;

    public Orders setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
        return this;
    }
}

