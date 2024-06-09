package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.ClubDto;
import com.chessgrinder.chessgrinder.dto.UserDto;
import com.chessgrinder.chessgrinder.entities.ClubEntity;
import com.chessgrinder.chessgrinder.mappers.ClubMapper;
import com.chessgrinder.chessgrinder.repositories.ClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClubService {
    private final ClubRepository clubRepository;
    private final ClubMapper clubMapper;

    public List<ClubDto> getAllClubs() {
        List<ClubEntity> clubs = clubRepository.findAll();

        return clubs.stream().map(clubMapper::toDto)
                .sorted(Comparator.comparing(ClubDto::getRegistrationDate))
                .collect(Collectors.toList());
    }
}
