package org.example.socialnetworkfx.socialnetworkfx.service;

import org.example.socialnetworkfx.socialnetworkfx.domain.Friendship;
import org.example.socialnetworkfx.socialnetworkfx.domain.ProfilePage;
import org.example.socialnetworkfx.socialnetworkfx.domain.User;
//import org.example.socialnetworkfx.socialnetworkfx.repository.FriendshipDbRepository;
import org.example.socialnetworkfx.socialnetworkfx.domain.event.ChangeEventType;
import org.example.socialnetworkfx.socialnetworkfx.domain.event.UserEntityChange;
import org.example.socialnetworkfx.socialnetworkfx.repository.NewRepository;
import org.example.socialnetworkfx.socialnetworkfx.repository.UserDbRepository;
import org.example.socialnetworkfx.socialnetworkfx.repository.paging.Page;
import org.example.socialnetworkfx.socialnetworkfx.repository.paging.Pageable;
import org.example.socialnetworkfx.socialnetworkfx.repository.paging.PageableImplementation;
import org.example.socialnetworkfx.socialnetworkfx.repository.paging.UserDbPagingRepository;
import org.example.socialnetworkfx.socialnetworkfx.utils.Observable;
import org.example.socialnetworkfx.socialnetworkfx.utils.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public class UserService implements Service<User>, Observable<UserEntityChange> {
    private UserDbPagingRepository repository;
    private List<Observer<UserEntityChange>> observers=new ArrayList<>();

    private int pageNumber = 0;
    private int pageSize = 10;

    public UserService(UserDbPagingRepository repository) {
        this.repository = repository;
    }

    @Override
    public User delete(Long ID) {
        User user = repository.findOne(ID).orElseThrow(() -> new IllegalArgumentException("User not found"));
        repository.delete(ID).orElseThrow(null);
        UserEntityChange event=new UserEntityChange(ChangeEventType.DELETE, user);
        notifyObservers(event);
        return user;
    }

    public User save(String firstName, String lastName,String email,String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(firstName, lastName, email, hashedPassword);
        User a=repository.save(newUser).orElse(null);
        UserEntityChange event=new UserEntityChange(ChangeEventType.ADD,newUser);
        notifyObservers(event);
        return a;
    }

    public User update(Long ID, String firstName, String lastName, String email, String password) {
        User toBeUpdated = new User(firstName, lastName, email, password);
        toBeUpdated.setID(ID);
        User a = repository.update(toBeUpdated).orElse(null);
        UserEntityChange event = new UserEntityChange(ChangeEventType.UPDATE, a);
        notifyObservers(event);
        return a;
    }

    @Override
    public Iterable<User> findAll(){
        return repository.findAll();
    }

    public User findOne(Long ID) {
        return repository.findOne(ID).orElseThrow(()-> new IllegalArgumentException("User not found"));
    }

    public User findByEmail(String email, String password) {
        Iterable<User> users = repository.findAll();
        for (User user : users) {
            if (user.getEmail().equals(email) && BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public Long findUserByNames(String firstName, String lastName) {
        Iterable<User> users = repository.findAll();
        for (User user : users) {
            if (user.getFirstName().equals(firstName) && user.getLastName().equals(lastName)) {
                return user.getID();
            }
        }
        return null;
    }

    public Page<User> findFriends() {
        Pageable pageable = new PageableImplementation(pageNumber, pageSize);
        return repository.findAll(pageable);
    }

    @Override
    public void addObserver(Observer<UserEntityChange> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UserEntityChange> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(UserEntityChange t) {
        observers.stream().forEach(x->x.update(t));
    }

    public ProfilePage getProfilePage(Long userId) {
        User user = findOne(userId);
        String fullName = user.getFirstName() + " " + user.getLastName();
        return new ProfilePage(fullName);
    }
}

