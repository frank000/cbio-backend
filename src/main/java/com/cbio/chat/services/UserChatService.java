package com.cbio.chat.services;

import com.cbio.app.repository.AttendantRepository;
import com.cbio.chat.dto.NotificationDTO;
import com.cbio.chat.dto.RegistrationDTO;
import com.cbio.chat.dto.UserDTO;
import com.cbio.chat.exceptions.UserNotFoundException;
import com.cbio.chat.interfaces.IUserService;
import com.cbio.chat.models.UserChatEntity;
import com.cbio.chat.repositories.UserRepository;
import com.cbio.chat.strategies.IUserRetrievalStrategy;
import com.cbio.chat.strategies.UserRetrievalByEmailStrategy;
import com.cbio.chat.strategies.UserRetrievalByIdStrategy;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserChatService implements UserDetailsService, IUserService {

  private final UserRepository userRepository;
  private final AttendantRepository attendantRepository;

//  private final SimpMessagingTemplate simpMessagingTemplate;
  

  private final BeanFactory beanFactory;


  private <T> UserChatEntity getUser(T userIdentifier, IUserRetrievalStrategy<T> strategy)
      throws UserNotFoundException {
    UserChatEntity user = strategy.getUser(userIdentifier);

    if (user == null) { throw new UserNotFoundException("User not found."); }

    return user;
  }
  private <T> UserChatEntity getUserByUsername(T userIdentifier, IUserRetrievalStrategy<T> strategy)
      throws UserNotFoundException {
    UserChatEntity user = strategy.getUserByUsername(userIdentifier);

    if (user == null) { throw new UserNotFoundException("User not found."); }

    return user;
  }

  public UserChatEntity getUser(String userId)
      throws BeansException, UserNotFoundException {
    return this.getUser(userId, beanFactory.getBean(UserRetrievalByIdStrategy.class));
  }

  @Override
  public UserChatEntity getUser(SecurityContext securityContext) throws BeansException, UserNotFoundException {
    return null;
  }

  public UserChatEntity getUserByUsername(String username)
      throws BeansException, UserNotFoundException {
    return this.getUserByUsername(username, beanFactory.getBean(UserRetrievalByEmailStrategy.class));
  }



  @Override
  public UserDetails loadUserByUsername(String email) {
//    User user = userRepository.findByEmail(email);
//
//    if (user == null) { return null; }
//
//    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
//      user.getEmail(),
//      user.getPassword(),
//      AuthorityUtils.createAuthorityList(user.getRole())
//    );
//
//    Authentication authentication = null;
//    try {
//      authentication = new UsernamePasswordAuthenticationToken(
//        userDetails,
//        null,
//        userDetails.getAuthorities()
//      );
//    } catch (Exception e) {}
//
//    SecurityContextHolder
//      .getContext()
//      .setAuthentication(authentication);
//
//    return userDetails;
    return null;
  }

  public boolean doesUserExist(String usename) {
    UserChatEntity user = userRepository.findByUsername(usename);

    return user != null;
  }

  public void addUser(RegistrationDTO registrationDTO)
      throws ValidationException {
    if (this.doesUserExist(registrationDTO.getFullName())) {
      throw new ValidationException("User already exists.");
    }

//    String encryptedPassword = new BCryptPasswordEncoder().encode(registrationDTO.getPassword());

    try {
      UserChatEntity user = UserChatEntity.builder()
              .username(registrationDTO.getFullName())
              .role("STANDARD-ROLE")
              .build();


 
      userRepository.save(user);
    } catch (ConstraintViolationException e) {
      throw new ValidationException(e.getConstraintViolations().iterator().next().getMessage());
    }
  }

  public List<UserDTO> retrieveFriendsList(UserChatEntity user) {
//    List<User> users = userRepository.findFriendsListFor(user.getEmail());

    return null;
  }
  
  public UserDTO retrieveUserInfo(UserChatEntity user) {
    return new UserDTO(
      user.getId(),
      user.getUsername()
    );
  }

  // TODO: switch to a TINYINT field called "numOfConnections" to add/subtract
  // the total amount of user connections
  public void setIsPresent(UserChatEntity user, Boolean stat) {
    user.setIsPresent(stat);

    userRepository.save(user);
  }

  public Boolean isPresent(UserChatEntity user) {
    return user.getIsPresent(); 
  }

  public void notifyUser(UserChatEntity recipientUser, NotificationDTO notification) {
    if (this.isPresent(recipientUser)) {
//      simpMessagingTemplate
//        .convertAndSend("/topic/user.notification." + recipientUser.getId(), notification);
    } else {
      System.out.println("sending email notification to " + recipientUser.getUsername());
      // TODO: send email
    }
  }
}