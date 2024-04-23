package ru.mai.khasanov.cryptowebmessenger.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Setter
@Getter
@Data
public class RoomRequest {
    private String name;
    private String encryptionAlgorithm;
    private String cipherMode;
    private String paddingMode;
}
