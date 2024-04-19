package ru.mai.khasanov.cryptowebmessenger.Cryptography.interfaces;

public interface IPadding {
    byte[] applyPadding(byte[] data, int blockSize);

    byte[] removePadding(byte[] data);
}
