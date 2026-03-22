package com.quazzom.mastermind.unit.controller;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.quazzom.mastermind.controller.UserController;
import com.quazzom.mastermind.dto.UserPasswordRequest;
import com.quazzom.mastermind.dto.UserProfileRequest;
import com.quazzom.mastermind.dto.UserProfileResponse;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.security.CustomUserDetails;
import com.quazzom.mastermind.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @Test
    void getProfileShouldReturnServiceData() {
        UUID uuidPublic = UUID.randomUUID();
        User user = new User();
        user.setName("Maria Silva");
        user.setEmail("maria@teste.com");
        user.setNickname("maria");
        user.setAge(25);
        UserProfileResponse expected = new UserProfileResponse(user);

        when(authentication.getName()).thenReturn(uuidPublic.toString());
        when(userService.getProfile(uuidPublic)).thenReturn(expected);

        ResponseEntity<UserProfileResponse> response = userController.getProfile(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void updateProfileShouldReturnUpdatedProfile() {
        UUID uuidPublic = UUID.randomUUID();
        UserProfileRequest request = new UserProfileRequest();
        request.setName("Maria Souza");
        request.setNickname("marias");
        request.setAge(26);

        User user = new User();
        user.setName("Maria Souza");
        user.setNickname("marias");
        user.setAge(26);
        UserProfileResponse expected = new UserProfileResponse(user);

        when(authentication.getName()).thenReturn(uuidPublic.toString());
        when(userService.updateProfile(uuidPublic, request)).thenReturn(expected);

        ResponseEntity<UserProfileResponse> response = userController.updateProfile(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void deleteProfileShouldReturnNoContent() {
        UUID uuidPublic = UUID.randomUUID();
        when(authentication.getName()).thenReturn(uuidPublic.toString());

        ResponseEntity<Void> response = userController.deleteProfile(authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteProfile(uuidPublic);
    }

    @Test
    void updatePasswordShouldReturnNoContent() {
        UUID uuidPublic = UUID.randomUUID();
        UserPasswordRequest request = new UserPasswordRequest();
        request.setCurrentPassword("Abc@123");
        request.setNewPassword("Def@456");
        when(authentication.getName()).thenReturn(uuidPublic.toString());

        ResponseEntity<Void> response = userController.updatePassword(request, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).updatePassword(uuidPublic, request);
    }

    @Test
    void getProfileShouldExtractUuidFromCustomUserDetails() {
        UUID uuidPublic = UUID.randomUUID();
        User user = new User();
        user.setId(10L);
        user.setUuidPublic(uuidPublic);
        CustomUserDetails details = new CustomUserDetails(user);

        UserProfileResponse expected = new UserProfileResponse(user);

        when(authentication.getPrincipal()).thenReturn(details);
        when(userService.getProfile(uuidPublic)).thenReturn(expected);

        ResponseEntity<UserProfileResponse> response = userController.getProfile(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getProfileShouldThrowUnauthorizedWhenAuthenticationIsInvalid() {
        when(authentication.getPrincipal()).thenReturn("invalidPrincipal");
        when(authentication.getName()).thenReturn("invalid");

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> userController.getProfile(authentication));

        assertEquals("Usuário não autenticado", exception.getMessage());
    }
}