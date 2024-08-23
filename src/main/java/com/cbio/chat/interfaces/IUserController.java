package com.cbio.chat.interfaces;

import com.cbio.chat.dto.RegistrationDTO;
import com.cbio.chat.exceptions.UserNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

public interface IUserController {
  ResponseEntity<String> register(@RequestBody RegistrationDTO registeringUser)
      throws ValidationException;

  ResponseEntity<String> retrieveRequestingUserFriendsList(Principal principal)
      throws UserNotFoundException;

  ResponseEntity<String> retrieveRequestUserInfo()
      throws UserNotFoundException;
}
