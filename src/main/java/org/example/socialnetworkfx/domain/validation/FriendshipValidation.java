package org.example.socialnetworkfx.socialnetworkfx.domain.validation;

import org.example.socialnetworkfx.socialnetworkfx.domain.Friendship;

public class FriendshipValidation implements Validation<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException{
        if(entity.getFirstFriend() == null || entity.getSecondFriend() == null){
            throw new ValidationException("First Friend cannot be null or Second Friend cannot be null");
        }
        if(entity.getFirstFriend().equals(entity.getSecondFriend())){
            throw new ValidationException("First Friend and Second Friend cannot be the same");
        }
    }
}
