package com.example.userauthenticationapi.service;

import com.example.userauthenticationapi.config.SendGridConfig;
import com.example.userauthenticationapi.dto.request.LoginUserDto;
import com.example.userauthenticationapi.dto.request.RegisterUserDto;
import com.example.userauthenticationapi.dto.request.ResendVerificationCodeDto;
import com.example.userauthenticationapi.dto.request.VerifyUserDto;
import com.example.userauthenticationapi.exception.ConflictException;
import com.example.userauthenticationapi.exception.ValidationException;
import com.example.userauthenticationapi.model.User;
import com.example.userauthenticationapi.repo.UserRepo;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private User testUser;

    @Mock
    private UserRepo userRepo;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService underTest;

    @BeforeEach
    void setUp() {
        testUser = Instancio.create(User.class);
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * Test {@link AuthenticationService#signUp(RegisterUserDto)}.
     *
     * <ul>
     *   <li>Given {@link UserRepo} {@link UserRepo#existsByEmail(String)} return {@code true}.
     * </ul>
     *
     * <p>Method under test: {@link AuthenticationService#signUp(RegisterUserDto)}
     */
    @Test
    @DisplayName("Test signUp(RegisterUserDto); given UserRepo existsByEmail(String) return 'true'")
    @Tag("MaintainedByDiffblue")
    void testSignUp_givenUserRepoExistsByEmailReturnTrue() {
        // Arrange
        UserRepo userRepo = mock(UserRepo.class);
        when(userRepo.existsByEmail(Mockito.<String>any())).thenReturn(true);

        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authManager = new ProviderManager(providers);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        EmailService emailService = new EmailService(new SendGridConfig());

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepo, authManager, passwordEncoder, emailService, new JwtService());

        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail("jane.doe@example.org");
        registerUserDto.setPassword("iloveyou");
        registerUserDto.setUsername("janedoe");

        // Act and Assert
        assertThrows(ConflictException.class, () -> authenticationService.signUp(registerUserDto));
        verify(userRepo).existsByEmail("jane.doe@example.org");
    }

    /**
     * Test {@link AuthenticationService#signUp(RegisterUserDto)}.
     *
     * <ul>
     *   <li>Given {@link UserRepo} {@link UserRepo#existsByEmail(String)} return {@code false}.
     *   <li>Then calls {@link UserRepo#existsByUsername(String)}.
     * </ul>
     *
     * <p>Method under test: {@link AuthenticationService#signUp(RegisterUserDto)}
     */
    @Test
    @DisplayName(
            "Test signUp(RegisterUserDto); given UserRepo existsByEmail(String) return 'false'; then calls existsByUsername(String)")
    @Tag("MaintainedByDiffblue")
    void testSignUp_givenUserRepoExistsByEmailReturnFalse_thenCallsExistsByUsername() {
        // Arrange
        when(userRepo.existsByEmail(Mockito.<String>any())).thenReturn(false);
        when(userRepo.existsByUsername(Mockito.<String>any())).thenReturn(true);

        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail("jane.doe@example.org");
        registerUserDto.setPassword("iloveyou");
        registerUserDto.setUsername("janedoe");

        // Act and Assert
        assertThrows(ConflictException.class, () -> underTest.signUp(registerUserDto));
        verify(userRepo).existsByEmail("jane.doe@example.org");
        verify(userRepo).existsByUsername("janedoe");
    }

    @Test
    void testShouldSuccessfullyAuthenticate() {
        //  given
        LoginUserDto givenLoginDto = Instancio.create(LoginUserDto.class);
        String email = givenLoginDto.getEmail();
        String password = givenLoginDto.getPassword();

        given(userRepo.findByEmail(email)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(password, testUser.getPassword())).willReturn(true);
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(authentication.isAuthenticated()).willReturn(true);

        //  when
        underTest.authenticate(givenLoginDto);

        //  then
        assertNotNull(testUser.getVerificationCode());
        assertFalse(testUser.getVerificationCode().isEmpty());

        assertNotNull(testUser.getVerificationCodeExpiresAt());
        assertTrue(testUser.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now()));

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepo).save(testUser);
        verify(emailService).sendEmail(testUser);
    }

    @Test
    void testWillThrowWhenAuthenticateFails() {
        //  given
        LoginUserDto givenLoginDto = Instancio.create(LoginUserDto.class);
        String email = givenLoginDto.getEmail();
        String password = givenLoginDto.getPassword();

        given(userRepo.findByEmail(email)).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(password, testUser.getPassword())).willReturn(false);

        //  then
        assertThatThrownBy(() -> underTest.authenticate(givenLoginDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Password is incorrect.");
    }

    @Test
    void testShouldSuccessfullyVerifyUser() {
        //  given
        VerifyUserDto givenVerifyDto = Instancio.create(VerifyUserDto.class);
        String testJwt = "test-jwt-token-value";
        givenVerifyDto.setVerificationCode("123456");
        testUser.setVerificationCode("123456");
        testUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));

        given(userRepo.findByEmail(givenVerifyDto.getEmail())).willReturn(Optional.of(testUser));
        given(jwtService.generateToken(testUser.getUsername())).willReturn(testJwt);

        //  when
        String expected = underTest.verifyUser(givenVerifyDto);

        //  then
        assertTrue(testUser.isAccountVerified());
        assertNull(testUser.getVerificationCode());
        assertNull(testUser.getVerificationCodeExpiresAt());

        verify(userRepo).save(testUser);
        assertEquals(testJwt, expected);
    }

    @Test
    void resendVerificationCode() {
        //  given
        ResendVerificationCodeDto givenResendDto = Instancio.create(ResendVerificationCodeDto.class);
        String givenEmail = givenResendDto.getEmail();
        String givenNewVerificationCode = "123456";
        LocalDateTime givenNewVerificationCodeExpiresAt = LocalDateTime.now().plusMinutes(5);

        given(userRepo.findByEmail(givenEmail)).willReturn(Optional.of(testUser));

        testUser.setVerificationCode(givenNewVerificationCode);
        testUser.setVerificationCodeExpiresAt(givenNewVerificationCodeExpiresAt);

        //  when
        underTest.resendVerificationCode(givenResendDto);

        //  then
        assertEquals("123456", givenNewVerificationCode);
        assertTrue(testUser.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now()));

        verify(userRepo).save(testUser);
        verify(emailService).sendEmail(testUser);
    }
}