package com.cafe.crm.exceptions.services;

/**
 * Created on 9/12/2017.
 */
public class NullInputsIngredientsServiceException extends IngredientsServiceException {
    public String message;

    public NullInputsIngredientsServiceException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
