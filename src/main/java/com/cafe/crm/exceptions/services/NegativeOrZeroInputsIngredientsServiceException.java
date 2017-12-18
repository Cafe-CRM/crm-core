package com.cafe.crm.exceptions.services;


/**
 * Created on 9/12/2017.
 */
public class NegativeOrZeroInputsIngredientsServiceException extends IngredientsServiceException {
    public String message;

    public NegativeOrZeroInputsIngredientsServiceException(String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
