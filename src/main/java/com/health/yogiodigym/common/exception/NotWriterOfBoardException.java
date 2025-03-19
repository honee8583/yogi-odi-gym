package com.health.yogiodigym.common.exception;

import static com.health.yogiodigym.common.message.ErrorMessage.NOT_WRITER_OF_BOARD;

import org.springframework.http.HttpStatus;

public class NotWriterOfBoardException extends CustomException {

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return NOT_WRITER_OF_BOARD.getMessage();
    }
}
