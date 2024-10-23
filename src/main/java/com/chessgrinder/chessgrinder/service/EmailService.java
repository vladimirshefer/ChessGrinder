package com.chessgrinder.chessgrinder.service;

public interface EmailService {
    void sendSimpleMessage(
            String to, String subject, String text);
}
