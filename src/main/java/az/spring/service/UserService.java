package az.spring.service;

import az.spring.entity.User;
import az.spring.model.UserCriteria;
import az.spring.model.UserDto;
import az.spring.model.UserRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    List<UserRequest> getAllUsers();

    UserRequest getUserById(Long id);

    void saveUser(UserDto userDto);

    void updateUser(UserDto userDto , Long id);

    void deleteUser(Long id);

    UserDto isValid(String username);

    List<User> getFilteredUsers(UserCriteria userCriteria);

}
