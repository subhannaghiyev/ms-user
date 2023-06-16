package az.spring.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    Long id;
    String firstName;
    String lastName;
    Long age;
    Boolean isActive;
    String username;
}
