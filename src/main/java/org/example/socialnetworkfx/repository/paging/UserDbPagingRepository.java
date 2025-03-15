package org.example.socialnetworkfx.socialnetworkfx.repository.paging;

import org.example.socialnetworkfx.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.socialnetworkfx.domain.validation.Validation;
import org.example.socialnetworkfx.socialnetworkfx.repository.UserDbRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDbPagingRepository extends UserDbRepository implements PagingRepository<Long, User>{
    public UserDbPagingRepository(String url, String username, String password, Validation<User> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        List<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"USERS\" ORDER BY \"ID\" OFFSET ? LIMIT ?");
             ){
            statement.setInt(1, pageable.getPageSize()*pageable.getPageNumber());
            statement.setInt(2, pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long ID1 = resultSet.getLong("ID");
                String firstName1 = resultSet.getString("FirstName");
                String lastName1 = resultSet.getString("LastName");
                String email = resultSet.getString("Email");
                String password = resultSet.getString("Password");
                User user1 = new User(firstName1, lastName1, email, password);
                user1.setID(ID1);
                users.add(user1);
            }
            return new PageImplementation<User>(pageable, users.stream());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
