package com.shinkaji.solveza.api.usermanagement.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.usermanagement.application.command.RegisterUserCommand;
import com.shinkaji.solveza.api.usermanagement.application.query.GetUsersQuery;
import com.shinkaji.solveza.api.usermanagement.domain.model.User;
import com.shinkaji.solveza.api.usermanagement.domain.repository.RoleRepository;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserRepository;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserSearchCriteria;
import com.shinkaji.solveza.api.usermanagement.domain.service.UserValidationService;
import com.shinkaji.solveza.api.usermanagement.presentation.dto.UserDto;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserManagementUseCaseImplのテスト")
class UserManagementUseCaseImplTest {

  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;

  @Mock private UserValidationService userValidationService;

  private UserManagementUseCaseImpl userManagementUseCase;

  @BeforeEach
  void setUp() {
    userManagementUseCase =
        new UserManagementUseCaseImpl(userRepository, roleRepository, userValidationService);
  }

  @Test
  @DisplayName("新規ユーザーを登録できる")
  void registerOrUpdateUser_shouldCreateNewUser_whenUserNotExists() {
    // Given
    RegisterUserCommand command =
        new RegisterUserCommand("GOOGLE", "external-123", "テストユーザー", "test@example.com");

    when(userRepository.findByProviderAndExternalId(any(Provider.class), eq("external-123")))
        .thenReturn(Optional.empty());

    // When
    UserDto result = userManagementUseCase.registerOrUpdateUser(command);

    // Then
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertEquals("テストユーザー", savedUser.getName());
    assertEquals("test@example.com", savedUser.getEmail());
    assertEquals("GOOGLE", savedUser.getProvider().name());
    assertEquals("external-123", savedUser.getExternalId());

    assertNotNull(result);
    assertEquals("テストユーザー", result.name());
    assertEquals("test@example.com", result.email());
  }

  @Test
  @DisplayName("既存ユーザーを更新できる")
  void registerOrUpdateUser_shouldUpdateExistingUser_whenUserExists() {
    // Given
    RegisterUserCommand command =
        new RegisterUserCommand("GOOGLE", "external-123", "更新後ユーザー", "updated@example.com");

    User existingUser =
        User.create(Provider.google(), "external-123", "元のユーザー", "original@example.com");

    when(userRepository.findByProviderAndExternalId(any(Provider.class), eq("external-123")))
        .thenReturn(Optional.of(existingUser));

    // When
    UserDto result = userManagementUseCase.registerOrUpdateUser(command);

    // Then
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertEquals("更新後ユーザー", savedUser.getName());
    assertEquals("updated@example.com", savedUser.getEmail());

    assertNotNull(result);
    assertEquals("更新後ユーザー", result.name());
    assertEquals("updated@example.com", result.email());
  }

  @Test
  @DisplayName("全ユーザーを取得できる")
  void getUsers_shouldReturnAllUsers_whenNoCriteriaProvided() {
    // Given
    GetUsersQuery query = new GetUsersQuery(null, null);

    User user1 = User.create(Provider.google(), "external-1", "ユーザー1", "user1@example.com");
    User user2 = User.create(Provider.github(), "external-2", "ユーザー2", "user2@example.com");
    List<User> users = Arrays.asList(user1, user2);

    when(userRepository.findByCriteria(any(UserSearchCriteria.class))).thenReturn(users);

    // When
    List<UserDto> result = userManagementUseCase.getUsers(query);

    // Then
    ArgumentCaptor<UserSearchCriteria> criteriaCaptor =
        ArgumentCaptor.forClass(UserSearchCriteria.class);
    verify(userRepository).findByCriteria(criteriaCaptor.capture());

    UserSearchCriteria capturedCriteria = criteriaCaptor.getValue();
    assertNull(capturedCriteria.provider());
    assertNull(capturedCriteria.externalId());

    assertEquals(2, result.size());
    assertEquals("ユーザー1", result.get(0).name());
    assertEquals("ユーザー2", result.get(1).name());
  }

  @Test
  @DisplayName("プロバイダーと外部IDで特定ユーザーを検索できる")
  void getUsers_shouldReturnFilteredUsers_whenCriteriaProvided() {
    // Given
    GetUsersQuery query = new GetUsersQuery("google", "external-123");

    User user = User.create(Provider.google(), "external-123", "Googleユーザー", "google@example.com");
    List<User> users = Collections.singletonList(user);

    when(userRepository.findByCriteria(any(UserSearchCriteria.class))).thenReturn(users);

    // When
    List<UserDto> result = userManagementUseCase.getUsers(query);

    // Then
    ArgumentCaptor<UserSearchCriteria> criteriaCaptor =
        ArgumentCaptor.forClass(UserSearchCriteria.class);
    verify(userRepository).findByCriteria(criteriaCaptor.capture());

    UserSearchCriteria capturedCriteria = criteriaCaptor.getValue();
    assertEquals("google", capturedCriteria.provider());
    assertEquals("external-123", capturedCriteria.externalId());

    assertEquals(1, result.size());
    assertEquals("Googleユーザー", result.get(0).name());
    assertEquals("GOOGLE", result.get(0).provider());
    assertEquals("external-123", result.get(0).externalId());
  }

  @Test
  @DisplayName("プロバイダーのみで検索できる")
  void getUsers_shouldReturnFilteredUsers_whenOnlyProviderProvided() {
    // Given
    GetUsersQuery query = new GetUsersQuery("github", null);

    User user1 = User.create(Provider.github(), "external-1", "GitHubユーザー1", "github1@example.com");
    User user2 = User.create(Provider.github(), "external-2", "GitHubユーザー2", "github2@example.com");
    List<User> users = Arrays.asList(user1, user2);

    when(userRepository.findByCriteria(any(UserSearchCriteria.class))).thenReturn(users);

    // When
    List<UserDto> result = userManagementUseCase.getUsers(query);

    // Then
    ArgumentCaptor<UserSearchCriteria> criteriaCaptor =
        ArgumentCaptor.forClass(UserSearchCriteria.class);
    verify(userRepository).findByCriteria(criteriaCaptor.capture());

    UserSearchCriteria capturedCriteria = criteriaCaptor.getValue();
    assertEquals("github", capturedCriteria.provider());
    assertNull(capturedCriteria.externalId());

    assertEquals(2, result.size());
    result.forEach(user -> assertEquals("GITHUB", user.provider()));
  }

  @Test
  @DisplayName("該当ユーザーが存在しない場合は空のリストを返す")
  void getUsers_shouldReturnEmptyList_whenNoUsersMatch() {
    // Given
    GetUsersQuery query = new GetUsersQuery("nonexistent", "nonexistent");

    when(userRepository.findByCriteria(any(UserSearchCriteria.class)))
        .thenReturn(Collections.emptyList());

    // When
    List<UserDto> result = userManagementUseCase.getUsers(query);

    // Then
    verify(userRepository).findByCriteria(any(UserSearchCriteria.class));
    assertTrue(result.isEmpty());
  }
}
