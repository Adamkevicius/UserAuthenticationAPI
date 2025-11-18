package com.example.userauthenticationapi.service.mapper;

import com.example.userauthenticationapi.dto.response.DeleteUserResponse;
import com.example.userauthenticationapi.dto.response.UpdateUserResponse;
import com.example.userauthenticationapi.dto.response.UserResponse;
import com.example.userauthenticationapi.model.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper underTest;

    private User givenUser;

    @BeforeEach
    void setUp() {
        //  given
        this.givenUser = Instancio.create(User.class);
    }

    @Test
    void testShouldReturnToDto() {
        //  when
        UserResponse expected = underTest.toDto(givenUser);

        //  then
        assertEquals(givenUser.getId(), expected.getId());
        assertEquals(givenUser.getEmail(), expected.getEmail());
        assertEquals(givenUser.getUsername(), expected.getUsername());
        assertEquals(givenUser.getCreatedAt(), expected.getCreatedAt());
    }

    @Test
    void testShouldReturnToUpdateDto() {
        //  when
        UpdateUserResponse expected = underTest.toUpdateDto(givenUser);

        //  then
        assertEquals(givenUser.getId(), expected.getId());
        assertEquals(givenUser.getEmail(), expected.getEmail());
        assertEquals(givenUser.getUsername(), expected.getUsername());
        assertEquals(givenUser.getUpdatedAt(), expected.getUpdatedAt());
    }

    @Test
    void testShouldReturnToDeleteUserDto() {
        //  when
        DeleteUserResponse expected = underTest.toDeleteUserDto(givenUser, LocalDateTime.now());

        //  then
        assertEquals(givenUser.getId(), expected.getId());
        assertEquals(givenUser.getEmail(), expected.getEmail());
        assertEquals(givenUser.getUsername(), expected.getUsername());
    }

    @Test
    void testShouldReturnToListDto() {
        //  given
        List<User> givenUserList = Instancio.ofList(User.class).size(5).create();

        //  when
        List<UserResponse> expected = underTest.toListDto(givenUserList);

        //  then
        assertEquals(givenUserList.size(), expected.size());
        for (int i = 0; i < givenUserList.size(); i++) {
            assertEquals(givenUserList.get(i).getId(), expected.get(i).getId());
        }
    }
}