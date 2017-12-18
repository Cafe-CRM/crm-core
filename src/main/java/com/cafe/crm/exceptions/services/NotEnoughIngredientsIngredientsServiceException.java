package com.cafe.crm.exceptions.services;

/**
 * Created on 9/12/2017.
 */
public class NotEnoughIngredientsIngredientsServiceException extends IngredientsServiceException {
    public String message;

    public NotEnoughIngredientsIngredientsServiceException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
