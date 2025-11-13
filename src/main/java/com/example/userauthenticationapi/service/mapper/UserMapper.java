package com.example.userauthenticationapi.service.mapper;

import com.example.userauthenticationapi.dto.response.DeleteUserResponse;
import com.example.userauthenticationapi.dto.response.UpdateUserResponse;
import com.example.userauthenticationapi.dto.response.UserResponse;
import com.example.userauthenticationapi.model.User;
import com.example.userauthenticationapi.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepo userRepo;

    public UserResponse toDto(User user) {
        return new UserResponse(
          user.getId(),
          user.getEmail(),
          user.getUsername(),
          user.getCreatedAt()
        );
    }

    public UpdateUserResponse toUpdateDto(User user) {
        return new UpdateUserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getUpdatedAt()
        );
    }

    public DeleteUserResponse toDeleteUserDto(User user, LocalDateTime deletedAt) {
        return new DeleteUserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                deletedAt
        );
    }

    public List<UserResponse> toListDto(List<User> userList) {
        List<UserResponse> userListDto = new ArrayList<>();

        for (User user : userList) {
            userListDto.add(toDto(user));
        }

        return userListDto;
    }
}
