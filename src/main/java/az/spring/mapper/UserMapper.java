package az.spring.mapper;

import az.spring.entity.User;
import az.spring.model.UserDto;
import az.spring.model.UserRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface UserMapper {

    UserDto mapEntityToDto(User user);


    User mapDtoToEntity(UserDto userDto);

    UserRequest mapEntityToRequest(User user);

}
