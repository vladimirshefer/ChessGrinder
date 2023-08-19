package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.boot.context.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Component
@RequiredArgsConstructor
public class TestDataInserter {

    final UserRepository userRepository;
    final BadgeRepository badgeRepository;
    final UserBadgeRepository userBadgeRepository;

    @SneakyThrows
    @EventListener(ApplicationStartedEvent.class)
    @Transactional
    public void init() {

        User user1 = userRepository.save(User.builder().id(UUID.randomUUID()).name("Vladimir Shefer").build());
        User user2 = userRepository.save(User.builder().id(UUID.randomUUID()).name("Alexander Boldyrev").build());
        User user3 = userRepository.save(User.builder().id(UUID.randomUUID()).name("Statislav Malov").build());
        User user4 = userRepository.save(User.builder().id(UUID.randomUUID()).name("Malik Rezaev").build());

        Badge badge1 = badgeRepository.save(Badge.builder().id(UUID.randomUUID())
                .description("For 300 lari donation!")
                .title("300 lari")
                .pictureUrl("🐝")
                .build());

        Badge badge2 = badgeRepository.save(Badge.builder().id(UUID.randomUUID())
                .description("Win 3 tournaments in tryhard league!")
                .title("3 wins in tryhard")
                .pictureUrl("🦀")
                .build());


        Thread.sleep(1000);

        UserBadge userBadge1 = userBadgeRepository.save(UserBadge.builder().id(UUID.randomUUID()).user(user1).badge(badge1).build());
        UserBadge userBadge2 = userBadgeRepository.save(UserBadge.builder().id(UUID.randomUUID()).user(user4).badge(badge2).build());
        UserBadge userBadge3 = userBadgeRepository.save(UserBadge.builder().id(UUID.randomUUID()).user(user3).badge(badge1).build());
        UserBadge userBadge4 = userBadgeRepository.save(UserBadge.builder().id(UUID.randomUUID()).user(user3).badge(badge2).build());

    }
}
