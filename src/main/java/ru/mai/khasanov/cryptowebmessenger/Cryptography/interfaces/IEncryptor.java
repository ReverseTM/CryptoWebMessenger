package ru.mai.khasanov.cryptowebmessenger.Cryptography.interfaces;

public interface IEncryptor {
    byte[] encode(byte[] data);

    byte[] decode(byte[] data);

    void setKeys(byte[] key);
    int getBlockLength();
}
