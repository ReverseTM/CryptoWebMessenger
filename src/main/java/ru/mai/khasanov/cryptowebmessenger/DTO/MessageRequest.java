package ru.mai.khasanov.cryptowebmessenger.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageRequest {
    private String username;
    private String message;
}
