package com.cafe.crm.services.impl.menu;

import com.cafe.crm.models.menu.Ingredients;
import com.cafe.crm.services.interfaces.menu.IngredientsService;
import com.cafe.crm.utils.CompanyIdCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IngredientsServiceReduceIngredientAmountTest {

    @Autowired
    private IngredientsService ingredientsService;

    @MockBean
    private CompanyIdCache companyIdCache;

    private Ingredients ingredients1;
    private Ingredients ingredients2;
    private Map<Ingredients, Double> recipe;

    private void initIngredientsAmounts(Double IngAmount1, Double IngAmount2, Double recipeAmount1, Double recipeAmount2) {
        ingredients1 = new Ingredients("ingredients1", "big", IngAmount1, 2D);
        ingredients2 = new Ingredients("ingredients2", "big", IngAmount2, 4D);

        recipe = new HashMap<>();
        recipe.put(ingredients1, recipeAmount1);
        recipe.put(ingredients2, recipeAmount2);
    }

    @Test
    public void testReduceIngredientAmount__correctInput__matchesExpected() {
        initIngredientsAmounts(2D, 4D, 2D, 2D);
        when(companyIdCache.getCompanyId()).thenReturn(1L);
        ingredientsService.reduceIngredientAmount(recipe);
        assertEquals(0, ingredients1.getAmount(), 0);
        assertEquals(2, ingredients2.getAmount(), 0);
    }

    /*@Test(expected = NegativeOrZeroInputsIngredientsServiceException.class)
    public void testReduceIngredientAmount__zeroRecipeAmount__matchesExpected() {
        initIngredientsAmounts(2, 4, 0, 0);

        ingredientsService.reduceIngredientAmount(recipe);
        assertEquals(2, ingredients1.getAmount(), 0);
        assertEquals(4, ingredients2.getAmount(), 0);
    }

    @Test(expected = NotEnoughIngredientsIngredientsServiceException.class)
    public void testReduceIngredientAmount__zeroIngredientAmount__matchesExpected() {
        initIngredientsAmounts(0, 0, 2, 1);

        ingredientsService.reduceIngredientAmount(recipe);
        assertEquals(0, ingredients1.getAmount(), 0);
        assertEquals(0, ingredients2.getAmount(), 0);
    }

    @Test(expected = NotEnoughIngredientsIngredientsServiceException.class)
    public void testReduceIngredientAmount__notEnoughIngredients__raisesException() {
        initIngredientsAmounts(2, 1, 2, 2);

        ingredientsService.reduceIngredientAmount(recipe);
    }

    @Test(expected = NotEnoughIngredientsIngredientsServiceException.class)
    public void testReduceIngredientAmount__negativeIngredientAmount__raisesException() {
        initIngredientsAmounts(-1, -2, 2, 1);

        ingredientsService.reduceIngredientAmount(recipe);
    }

    @Test(expected = NullInputsIngredientsServiceException.class)
    public void testReduceIngredientAmount__nullRecipe__raisesException() {
        ingredientsService.reduceIngredientAmount(null);
    }

    @Test(expected = NullInputsIngredientsServiceException.class)
    public void testReduceIngredientAmount__nullIngredientInRecipe__raisesException() {
        initIngredientsAmounts(2, 4, 2, 1);
        recipe.put(null, 2);

        ingredientsService.reduceIngredientAmount(recipe);
    }*/

}
