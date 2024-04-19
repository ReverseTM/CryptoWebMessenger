package ru.mai.khasanov.cryptowebmessenger.Cryptography.UtilFunctions;

import java.security.SecureRandom;

public class IVGenerator {
    public static byte[] generateIV(int size) {
        byte[] IV = new byte[size];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);

        return IV;
    }
}
