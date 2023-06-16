package az.spring.controller;

import az.spring.config.RedisTokenService;
import az.spring.entity.User;
import az.spring.exception.*;
import az.spring.mapper.UserMapper;
import az.spring.model.LoginRequest;
import az.spring.model.UserCriteria;
import az.spring.model.UserDto;
import az.spring.model.UserRequest;
import az.spring.service.UserService;
import az.spring.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final RedisTokenService redisTokenService;

    private final UserMapper userMapper;

    private String previousToken;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserRequest> getAllUsers(@RequestHeader String token, @ModelAttribute UserCriteria userCriteria) {
        boolean isValidToken = redisTokenService.isValidToken(token);
        boolean hasExpired = redisTokenService.hasTokenExpired(token);

        System.out.println("Previous Token: " + previousToken);
        System.out.println("Current Token: " + token);

        if (!isValidToken && !token.equals(previousToken)) {
            log.warn("Invalid token: " + token);
            throw new TokenIsNotValidException("Token is not valid");
        } else if (hasExpired) {
            log.warn("Token has expired: " + token);
            throw new UnAuthorizedException("Token has expired");
        } else {
            List<User> filteredUsers = userService.getFilteredUsers(userCriteria);

            List<UserRequest> userRequestList = new ArrayList<>();
            for (User user : filteredUsers) {
                UserRequest userRequest = userMapper.mapEntityToRequest(user);
                userRequestList.add(userRequest);
            }

            previousToken = token; // Update the previous token to the current token

            return userRequestList;
        }
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserRequest getUserById(@PathVariable Long id, @RequestHeader String token) {
        boolean validToken = redisTokenService.isValidToken(token);
        if (!validToken && !token.equals(previousToken)) {
            log.warn("Invalid token: " + token);
            throw new TokenIsNotValidException("Token is not valid");
        }
        boolean hasExpired = redisTokenService.hasTokenExpired(token);
        if (hasExpired) {
            log.warn("Token has expired: " + token);
            throw new UnAuthorizedException("Token has expired");
        }

        previousToken = token;

        return userService.getUserById(id);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> saveUser(@RequestBody UserDto userDto) {
        UserDto valid = userService.isValid(userDto.getUsername());
        if (valid != null && valid.getUsername().equalsIgnoreCase(userDto.getUsername())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        userService.saveUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @PutMapping("/update/{id}")
    public void updateUser(@RequestBody UserDto userDto, @PathVariable Long id, @RequestHeader String token) {
        boolean validToken = redisTokenService.isValidToken(token);
        if (!validToken && !token.equals(previousToken)) {
            log.warn("Invalid token: " + token);
            throw new TokenIsNotValidException("Token is not valid");
        }
        boolean hasExpired = redisTokenService.hasTokenExpired(token);
        if (hasExpired) {
            log.warn("Token has expired: " + token);
            throw new UnAuthorizedException("Token has expired");
        }
        previousToken = token;

        userService.updateUser(userDto, id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id, @RequestHeader String token) {
        boolean validToken = redisTokenService.isValidToken(token);
        if (!validToken && !token.equals(previousToken)) {
            log.warn("Invalid token: " + token);
            throw new TokenIsNotValidException("Token is not valid");
        }
        boolean hasExpired = redisTokenService.hasTokenExpired(token);
        if (hasExpired) {
            log.warn("Token has expired: " + token);
            throw new UnAuthorizedException("Token has expired");
        }

        previousToken = token;
        userService.deleteUser(id);
    }


    @PostMapping("/login")
    public ResponseEntity<String> isValid(@RequestBody LoginRequest loginRequest) {
        UserDto valid = userService.isValid(loginRequest.getUsername());

        if (valid != null && valid.getUsername().equalsIgnoreCase(loginRequest.getUsername())) {
            // Check the password
            if (PasswordUtils.checkPassword(loginRequest.getPassword(), valid.getPassword())) {
                String token = redisTokenService.generateToken(valid.getId());
                redisTokenService.saveToken(valid.getId(), token);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", token);
                return ResponseEntity.ok().headers(headers).body(token);
            } else {
                throw new InvalidPasswordException("Invalid password");
            }
        } else {
            throw new UserNotFoundException("User Not Found");
        }
    }


    @PostMapping("/resetToken")
    public ResponseEntity<String> resetToken(@RequestHeader String token) {
        String newToken = redisTokenService.resetToken(token);
        return ResponseEntity.ok(newToken);
    }

}
