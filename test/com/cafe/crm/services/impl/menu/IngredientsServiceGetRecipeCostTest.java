package com.cafe.crm.services.impl.menu;

import com.cafe.crm.exceptions.services.NegativeOrZeroInputsIngredientsServiceException;
import com.cafe.crm.exceptions.services.NullInputsIngredientsServiceException;
import com.cafe.crm.models.menu.Ingredients;
import com.cafe.crm.services.interfaces.menu.IngredientsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IngredientsServiceGetRecipeCostTest {

    @Autowired
    IngredientsService ingredientsService;

    @Test
    public void testGetRecipeCost__hasRecipes__matchesExpected() {
        Ingredients ingredients1 = new Ingredients("ingredients1", "big", 2D, 2D);
        Ingredients ingredients2 = new Ingredients("ingredients2", "big", 4D, 4D);
        Map<Ingredients, Double> recipe = new HashMap<>();
        recipe.put(ingredients1, 2D);
        recipe.put(ingredients2, 2D);

        assertEquals(12D, ingredientsService.getRecipeCost(recipe), 0);
    }

    @Test
    public void testGetRecipeCost__emptyRecipes__matchesZero() {
        Map<Ingredients, Double> recipe = new HashMap<>();
        assertEquals(0D, ingredientsService.getRecipeCost(recipe), 0);
    }


    @Test(expected = NullInputsIngredientsServiceException.class)
    public void testGetRecipeCost__nullRecipesMap__exception() {
        ingredientsService.getRecipeCost(null);
    }


    @Test(expected = NullInputsIngredientsServiceException.class)
    public void testGetRecipeCost__nullRecipesMapContent__exception() {
        Map<Ingredients, Double> recipe = new HashMap<>();
        recipe.put(null, 2D);
        ingredientsService.getRecipeCost(recipe);
    }

    @Test(expected = NegativeOrZeroInputsIngredientsServiceException.class)
    public void testCreateRecipe__negativeAmountIngredient__raisesException() {
        Map<Ingredients, Double> map = new HashMap<>();
        Ingredients ingredients = new Ingredients();
        map.put(ingredients, -1D);
        ingredientsService.getRecipeCost(map);
    }

}
