package org.nandomattos.mapper;

import org.nandomattos.entity.User;
import org.nandomattos.model.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static List<UserDTO> listEntityToDto(List<User> userList){
        List<UserDTO> dtoList = new ArrayList<UserDTO>();

        for(User user : userList){
            dtoList.add(UserDTO.builder()
                    .ra(user.getRa())
                    .nome(user.getNome())
                    .senha(user.getSenha())
                    .build());
        }
        return dtoList;
    }
}
