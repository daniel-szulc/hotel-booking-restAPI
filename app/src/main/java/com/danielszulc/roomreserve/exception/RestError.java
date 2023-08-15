package com.danielszulc.roomreserve.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestError {

    private LocalDateTime timestamp;
    private String message;
    private String details;

}
