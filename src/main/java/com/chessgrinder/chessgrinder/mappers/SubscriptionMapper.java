package com.chessgrinder.chessgrinder.mappers;

import com.chessgrinder.chessgrinder.dto.BadgeDto;
import com.chessgrinder.chessgrinder.dto.SubscriptionDto;
import com.chessgrinder.chessgrinder.entities.BadgeEntity;
import com.chessgrinder.chessgrinder.entities.SubscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionMapper {

    public SubscriptionDto toDto(SubscriptionEntity subscription) {
    //TODO возможно, дополнить!
        return SubscriptionDto.builder()
                .id(subscription.getId().toString())
                .startDate(subscription.getStartDate())
                .build();
    }

    public List<SubscriptionDto> toDto(List<SubscriptionEntity> badgeEntities) {
        return badgeEntities.stream().map(this::toDto).toList();
    }
}
