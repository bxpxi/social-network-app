package org.example.socialnetworkfx.socialnetworkfx.domain.validation;

import org.example.socialnetworkfx.socialnetworkfx.domain.User;

public class UserValidation implements Validation<User>{
    @Override
    public void validate(User entity){
        if(entity.getFirstName().isEmpty() || entity.getLastName().isEmpty()){
            throw new ValidationException("First Name and Last Name cannot be empty");
        }
        if(!entity.getEmail().contains("@") || !entity.getEmail().contains(".com")){
            throw new ValidationException("Email address is not valid.");
        }
        if(entity.getPassword().length()<6){
            throw new ValidationException("Password must be at least 6 characters");
        }
        if(entity.getFirstName().matches("[A-Z][a-z]+")){
            throw new ValidationException("First name contains only letters");
        }
        if(entity.getLastName().matches("[A-Z][a-z]+")){
            throw new ValidationException("Last name contains only letters");
        }
    }

}
