package com.example.userauthenticationapi.service;

import com.example.userauthenticationapi.dto.request.RegisterUserDto;
import com.example.userauthenticationapi.dto.request.UpdateUserDto;
import com.example.userauthenticationapi.dto.request.UserPasswordUpdateDto;
import com.example.userauthenticationapi.dto.response.DeleteUserResponse;
import com.example.userauthenticationapi.dto.response.UpdateUserResponse;
import com.example.userauthenticationapi.dto.response.UserResponse;
import com.example.userauthenticationapi.exception.ConflictException;
import com.example.userauthenticationapi.exception.ResourceNotFoundException;
import com.example.userauthenticationapi.model.User;
import com.example.userauthenticationapi.model.enums.Role;
import com.example.userauthenticationapi.repo.UserRepo;
import com.example.userauthenticationapi.service.mapper.UserMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.will;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService underTest;

    @Test
    void testShouldCreate() {
        //  given
        RegisterUserDto givenUserDto = Instancio.create(RegisterUserDto.class);
        String givenEmail = givenUserDto.getEmail();
        String givenUsername = givenUserDto.getUsername();
        String givenPassword = givenUserDto.getPassword();

        given(userRepo.existsByEmail(givenEmail)).willReturn(false);
        given(userRepo.existsByUsername(givenUsername)).willReturn(false);
        given(passwordEncoder.encode(givenPassword)).willReturn("encodedPassword");

        //  when
        underTest.create(givenUserDto);

        //  then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepo).save(captor.capture());
        User savedUser = captor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo(givenEmail);
        assertThat(savedUser.getUsername()).isEqualTo(givenUsername);
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRole()).isEqualTo(Role.User);
        assertThat(savedUser.isAccountVerified()).isFalse();
    }

    @Test
    void testWillThrowWhenEmailIsTaken() {
        //  given
        RegisterUserDto givenUserDto = Instancio.create(RegisterUserDto.class);
        given(userRepo.existsByEmail(givenUserDto.getEmail())).willReturn(true);

        //  then
        assertThatThrownBy(() -> underTest.create(givenUserDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email is already registered.");

        verify(userRepo, never()).save(any());
    }

    @Test
    void testWillThrowWhenUsernameIsTaken() {
        //  given
        RegisterUserDto givenUserDto = Instancio.create(RegisterUserDto.class);
        given(userRepo.existsByUsername(givenUserDto.getUsername())).willReturn(true);

        //  then
        assertThatThrownBy(() -> underTest.create(givenUserDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Username already taken.");

        verify(userRepo, never()).save(any());
    }

    @Test
    void testShouldGetById() {
        //  given
        long id = 10;
        User givenUser = Instancio.create(User.class);
        givenUser.setId(id);

        UserResponse givenUserDto = Instancio.create(UserResponse.class);
        givenUserDto.setId(id);

        given(userRepo.findById(id)).willReturn(Optional.of(givenUser));
        given(userMapper.toDto(givenUser)).willReturn(givenUserDto);

        //  when
        UserResponse expected = underTest.getById(id);

        //  then
        verify(userRepo).findById(id);
        assertEquals(givenUserDto, expected);
    }

    @Test
    void testWillThrowWhenIdIsNegative() {
        //  given
        long id = -10;

        //  then
        assertThrows(ConflictException.class, () -> underTest.getById(id));
    }

    @Test
    void testShouldGetByUsername() {
        //  given
        String username = "testUsername";
        User givenUser = Instancio.create(User.class);
        givenUser.setUsername(username);

        UserResponse givenUserDto = Instancio.create(UserResponse.class);
        givenUserDto.setUsername(username);

        given(userRepo.findByUsername(username)).willReturn(Optional.of(givenUser));
        given(userMapper.toDto(givenUser)).willReturn(givenUserDto);

        //  when
        UserResponse expected = underTest.getByUsername(username);

        //  then
        verify(userRepo).findByUsername(username);
        assertEquals(givenUserDto, expected);
    }

    @Test
    void testShouldGetByEmail() {
        //  given
        String email = "test@gmail.com";
        User givenUser = Instancio.create(User.class);
        givenUser.setEmail(email);

        UserResponse givenUserDto = Instancio.create(UserResponse.class);
        givenUserDto.setEmail(email);

        given(userRepo.findByEmail(email)).willReturn(Optional.of(givenUser));
        given(userMapper.toDto(givenUser)).willReturn(givenUserDto);

        //  when
        UserResponse expected = underTest.getByEmail(email);

        //  then
        verify(userRepo).findByEmail(email);
        assertEquals(givenUserDto, expected);
    }

    @Test
    void testShouldGetAll() {
        //  given
        List<User> givenList = Instancio.ofList(User.class).size(5).create();
        List<UserResponse> givenDto = Instancio.ofList(UserResponse.class).size(5).create();

        given(userRepo.findAll()).willReturn(givenList);
        given(userMapper.toListDto(givenList)).willReturn(givenDto);

        //  when
        List<UserResponse> expected = underTest.getAll();

        //  then
        verify(userRepo).findAll();
        assertEquals(givenDto, expected);
    }

    @Test
    void testWillThrowWhenGetAllFails() {
        //  given
        given(userRepo.findAll()).willReturn(new ArrayList<>());

        //  then
        assertThatThrownBy(() -> underTest.getAll())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Users not found.");

    }

    @Test
    void testUpdateFieldsAreNotEmpty() {
        //  given
        String email = "test@example.com";
        String username = "john";
        String password = "secret123";

        //  then
        assertDoesNotThrow(() -> underTest
                .isUpdateFieldsValid(email , username, password));
    }

    @Test
    void testUpdateFieldsAreEmpty() {
        //  then
        // IF EMAIL IS EMPTY
        assertThatThrownBy(() -> underTest
                .isUpdateFieldsValid("" , "john", "secret123"));

        //  IF USERNAME IS EMPTY
        assertThatThrownBy(() -> underTest
                .isUpdateFieldsValid("John Doe" , "", "secret123"));

        //  IF PASSWORD IS EMPTY
        assertThatThrownBy(() -> underTest
                .isUpdateFieldsValid("test@example.com" , "john", ""));
    }

    @Test
    void testShouldUpdateById() {
        //  given
        long id = 10;
        User givenUser = Instancio.create(User.class);
        givenUser.setId(id);
        UpdateUserDto givenUpdateUserDto = Instancio.create(UpdateUserDto.class);

        UpdateUserResponse response = new UpdateUserResponse();

        given(userRepo.findById(id)).willReturn(Optional.of(givenUser));
        given(passwordEncoder.encode(givenUpdateUserDto.getPassword())).willReturn("encodedPassword");
        given(userMapper.toUpdateDto(givenUser)).willReturn(response);

        //  when
        UpdateUserResponse expected = underTest.updateById(id, givenUpdateUserDto);

        //  then
        verify(userRepo).save(givenUser);
        assertEquals(response, expected);
    }

    @Test
    void testShouldUpdateByUsername() {
        //  given
        String username = "test";
        User givenUser = Instancio.create(User.class);
        givenUser.setUsername(username);
        UpdateUserDto givenUpdateUserDto = Instancio.create(UpdateUserDto.class);

        UpdateUserResponse response = new UpdateUserResponse();

        given(userRepo.findByUsername(username)).willReturn(Optional.of(givenUser));
        given(passwordEncoder.encode(givenUpdateUserDto.getPassword())).willReturn("encodedPassword");
        given(userMapper.toUpdateDto(givenUser)).willReturn(response);

        //  when
        UpdateUserResponse expected = underTest.updateByUsername(username, givenUpdateUserDto);

        //  then
        verify(userRepo).save(givenUser);
        assertEquals(response, expected);
    }

    @Test
    void testShouldUpdateByEmail() {
        //  given
        String email = "test@gmail.com";
        User givenUser = Instancio.create(User.class);
        givenUser.setEmail(email);
        UpdateUserDto givenUpdateUserDto = Instancio.create(UpdateUserDto.class);

        UpdateUserResponse response = new UpdateUserResponse();

        given(userRepo.findByEmail(email)).willReturn(Optional.of(givenUser));
        given(passwordEncoder.encode(givenUpdateUserDto.getPassword())).willReturn("encodedPassword");
        given(userMapper.toUpdateDto(givenUser)).willReturn(response);

        //  when
        UpdateUserResponse expected = underTest.updateByEmail(email, givenUpdateUserDto);

        //  then
        verify(userRepo).save(givenUser);
        assertEquals(response, expected);
    }

    @Test
    void testShouldUpdatePasswordByEmail() {
        //  given
        User givenUser = Instancio.create(User.class);
        UserPasswordUpdateDto givenUpdateDto = Instancio.create(UserPasswordUpdateDto.class);
        UpdateUserResponse response = new UpdateUserResponse();

        given(userRepo.findByEmail(givenUpdateDto.getEmail())).willReturn(Optional.of(givenUser));
        given(passwordEncoder.encode(givenUpdateDto.getPassword())).willReturn("encodedPassword");
        given(userMapper.toUpdateDto(givenUser)).willReturn(response);

        //  when
        UpdateUserResponse expected = underTest.updatePasswordByEmail(givenUpdateDto);

        //  then
        verify(userRepo).findByEmail(givenUpdateDto.getEmail());
        assertEquals(response, expected);
    }

    @Test
    void testWillThrowWhenUpdatePasswordByEmailPasswordIsEmpty() {
        //  given
        User givenUser = Instancio.create(User.class);
        UserPasswordUpdateDto givenUpdateDto = Instancio.create(UserPasswordUpdateDto.class);
        givenUpdateDto.setPassword("");
        given(userRepo.findByEmail(givenUpdateDto.getEmail())).willReturn(Optional.of(givenUser));

        //  when
        ConflictException expected = assertThrows(
                ConflictException.class, () -> underTest.updatePasswordByEmail(givenUpdateDto)
        );

        //  then
        assertEquals("Password field must not be empty.", expected.getMessage());
    }

    @Test
    void testShouldDeleteById() {
        //  given
        long id  = 10;
        User givenUser = Instancio.create(User.class);
        givenUser.setId(id);
        DeleteUserResponse response = new DeleteUserResponse();

        given(userRepo.findById(id)).willReturn(Optional.of(givenUser));
        given(userMapper.toDeleteUserDto(eq(givenUser), any(LocalDateTime.class))).willReturn(response);

        //  when
        DeleteUserResponse expected = underTest.deleteById(id);

        //  then
        verify(userRepo).deleteById(id);
        assertEquals(response, expected);
    }

    @Test
    void testShouldDeleteByUsername() {
        //  given
        String username  = "test";
        User givenUser = Instancio.create(User.class);
        givenUser.setUsername(username);
        DeleteUserResponse response = new DeleteUserResponse();

        given(userRepo.findByUsername(username)).willReturn(Optional.of(givenUser));
        given(userMapper.toDeleteUserDto(eq(givenUser), any(LocalDateTime.class))).willReturn(response);

        //  when
        DeleteUserResponse expected = underTest.deleteByUsername(username);

        //  then
        verify(userRepo).deleteByUsername(username);
        assertEquals(response, expected);
    }

    @Test
    void testShouldDeleteByEmail() {
        //  given
        String email  = "test@gmail.com";
        User givenUser = Instancio.create(User.class);
        givenUser.setEmail(email);
        DeleteUserResponse response = new DeleteUserResponse();

        given(userRepo.findByEmail(email)).willReturn(Optional.of(givenUser));
        given(userMapper.toDeleteUserDto(eq(givenUser), any(LocalDateTime.class))).willReturn(response);

        //  when
        DeleteUserResponse expected = underTest.deleteByEmail(email);

        //  then
        verify(userRepo).deleteByEmail(email);
        assertEquals(response, expected);
    }

    @Test
    void testShouldDeleteAll() {
        //  given
        List<User> givenUserList = Instancio.ofList(User.class).size(5).create();

        given(userRepo.findAll()).willReturn(givenUserList);

        //  when
        underTest.deleteAll();

        //  then
        verify(userRepo).deleteAllInBatch();
    }

    @Test
    void testWillThrowWhenDeleteAllFails() {
        //  given
        given(userRepo.findAll()).willReturn(new ArrayList<>());

        //  then
        assertThatThrownBy(() -> underTest.deleteAll())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Users not found.");
    }
}