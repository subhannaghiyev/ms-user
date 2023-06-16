package az.spring.service.impl;

import az.spring.entity.User;
import az.spring.exception.UserNotFoundException;
import az.spring.mapper.UserMapper;
import az.spring.model.UserCriteria;
import az.spring.model.UserDto;
import az.spring.model.UserRequest;
import az.spring.repository.UserRepository;
import az.spring.service.UserService;
import az.spring.service.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.grammars.hql.HqlParser;
import org.hibernate.sql.ast.tree.cte.SearchClauseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;


    @Override
    public List<UserRequest> getAllUsers() {
        var users = userRepository.findAll();

        List<UserRequest> userRequestList = new ArrayList<>();

        for (User user : users){
            userRequestList.add(userMapper.mapEntityToRequest(user));
        }
        return userRequestList;
    }

    @Override
    public UserRequest getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("id is not exists"));
        if (!user.getIsActive()) {
            throw new UserNotFoundException("User not found");
        }
        return userMapper.mapEntityToRequest(user);
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = userMapper.mapDtoToEntity(userDto);
        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDto userDto, Long id) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("id is not exists"));

        User updatedUser = userMapper.mapDtoToEntity(userDto);
        updatedUser.setId(existingUser.getId());

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setIsActive(updatedUser.getIsActive());
        existingUser.setUsername(updatedUser.getUsername());

        userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("id is not exists"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public UserDto isValid(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getUsername().equalsIgnoreCase(username)) {
            return userMapper.mapEntityToDto(user);
        } else {
            return null;
        }
    }
    @Override
    public List<User> getFilteredUsers(UserCriteria userCriteria) {
        Specification<User> specification = new UserSpecification(userCriteria);
        return userRepository.findAll(specification);
    }
}
