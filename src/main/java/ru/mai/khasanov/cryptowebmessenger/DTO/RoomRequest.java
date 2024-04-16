package ru.mai.khasanov.cryptowebmessenger.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoomRequest {
    private String name;
    private String encryptionAlgorithm;
    private String cipherMode;
    private String paddingMode;
}
