package ru.mai.khasanov.cryptowebmessenger.Cryptography.Padding;

import ru.mai.khasanov.cryptowebmessenger.Cryptography.interfaces.IPadding;

public class PaddingMode {
    public enum Mode {
        Zeros,
        ANSI_X_923,
        PKCS7,
        ISO_10126
    }

    public static IPadding getInstance(PaddingMode.Mode mode) {
        return switch (mode) {
            case Zeros -> new Zeros();
            case ANSI_X_923 -> new ANSI_X_923();
            case PKCS7 -> new PKCS7();
            case ISO_10126 -> new ISO_10126();
        };
    }

}
