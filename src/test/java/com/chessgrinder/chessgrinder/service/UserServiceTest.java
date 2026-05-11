package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.UserDto;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleService roleService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllUsers_shouldUseDefaultOrderedList_whenNoQueryNoCityNoSort() {
        UserEntity user = createUser("default");
        stubMappedPage(user, () -> userRepository.findAllOrdered(any(Pageable.class)));

        List<UserDto> result = userService.getAllUsers(2, 25, null, null, null);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAllOrdered(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(2);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(25);
        assertThat(result).hasSize(1);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_shouldUseReputationList_whenSortIsReputation() {
        UserEntity user = createUser("reputation");
        stubMappedPage(user, () -> userRepository.findAllOrderedByReputation(any(Pageable.class)));

        userService.getAllUsers(1, 10, "reputation", null, null);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAllOrderedByReputation(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_shouldUseCityRatingList_whenCityProvided() {
        UserEntity user = createUser("city-rating");
        stubMappedPage(user, () -> userRepository.findAllOrderedByCity(eq("Berlin"), any(Pageable.class)));

        userService.getAllUsers(0, 15, null, "Berlin", null);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAllOrderedByCity(eq("Berlin"), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(15);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_shouldUseCityReputationList_whenCityAndReputationSortProvided() {
        UserEntity user = createUser("city-reputation");
        stubMappedPage(user, () -> userRepository.findAllByCityOrderedByReputation(eq("Berlin"), any(Pageable.class)));

        userService.getAllUsers(3, 5, "reputation", "Berlin", null);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAllByCityOrderedByReputation(eq("Berlin"), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(3);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_shouldUseAdminSearch_whenQueryProvided() {
        setAuthenticatedUser(true);
        UserEntity user = createUser("admin-search");
        stubMappedPage(user, () -> userRepository.searchAllOrdered(eq("term"), any(Pageable.class)));

        userService.getAllUsers(5, 100, "reputation", "Berlin", "term");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).searchAllOrdered(eq("term"), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
        verify(userRepository, never()).findAllOrdered(any(Pageable.class));
        verify(userRepository, never()).findAllOrderedByCity(anyString(), any(Pageable.class));
        verify(userRepository, never()).findAllOrderedByReputation(any(Pageable.class));
        verify(userRepository, never()).findAllByCityOrderedByReputation(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_shouldUsePublicSearch_whenQueryProvided() {
        setAuthenticatedUser(false);
        UserEntity user = createUser("public-search");
        stubMappedPage(user, () -> userRepository.searchAllOrderedPublic(eq("term"), any(Pageable.class)));

        userService.getAllUsers(1, 1, null, null, "term");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).searchAllOrderedPublic(eq("term"), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
        verify(userRepository, never()).findAllOrdered(any(Pageable.class));
        verify(userRepository, never()).findAllOrderedByCity(anyString(), any(Pageable.class));
        verify(userRepository, never()).findAllOrderedByReputation(any(Pageable.class));
        verify(userRepository, never()).findAllByCityOrderedByReputation(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_shouldFallbackToList_whenQueryIsBlank() {
        UserEntity user = createUser("blank-query");
        stubMappedPage(user, () -> userRepository.findAllOrdered(any(Pageable.class)));

        userService.getAllUsers(0, 50, null, null, "   ");

        verify(userRepository).findAllOrdered(any(Pageable.class));
        verify(userRepository, never()).searchAllOrdered(anyString(), any(Pageable.class));
        verify(userRepository, never()).searchAllOrderedPublic(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(userRepository);
    }

    private void stubMappedPage(UserEntity user, RepositoryCall repositoryCall) {
        UserDto userDto = UserDto.builder().id(user.getId().toString()).name(user.getName()).build();
        when(repositoryCall.invoke()).thenReturn(new PageImpl<>(List.of(user)));
        when(userMapper.toDto(user)).thenReturn(userDto);
    }

    private static UserEntity createUser(String name) {
        return UserEntity.builder()
                .id(UUID.randomUUID())
                .name(name)
                .roles(List.of())
                .build();
    }

    private static void setAuthenticatedUser(boolean isAdmin) {
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .roles(isAdmin ? List.of(RoleEntity.builder().name(RoleEntity.Roles.ADMIN).build()) : List.of())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of())
        );
    }

    @FunctionalInterface
    private interface RepositoryCall {
        Page<UserEntity> invoke();
    }
}
