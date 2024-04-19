package ru.mai.khasanov.cryptowebmessenger.Cryptography.DES;

import ru.mai.khasanov.cryptowebmessenger.Cryptography.ExpandKey.DESKeyExpand;
import ru.mai.khasanov.cryptowebmessenger.Cryptography.FeistelNetwork.FeistelNetwork;
import ru.mai.khasanov.cryptowebmessenger.Cryptography.RoundFunction.FeistelFunction;
import ru.mai.khasanov.cryptowebmessenger.Cryptography.UtilFunctions.Util;
import ru.mai.khasanov.cryptowebmessenger.Cryptography.interfaces.IEncryptor;

public class DES implements IEncryptor {
    public static final int[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7
    };

    public static final int[] P = {
            40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25
    };

    private final IEncryptor feistelNetwork;

    public DES() {
        this.feistelNetwork = new FeistelNetwork(new DESKeyExpand(16), new FeistelFunction(), 16);
    }

    @Override
    public byte[] encode(byte[] data) {
        var cipherText = feistelNetwork.encode(Util.permutation(data, IP, false, 1));
        return Util.permutation(cipherText, P, false, 1);
    }

    @Override
    public byte[] decode(byte[] data) {
        var cipherText = feistelNetwork.decode(Util.permutation(data, IP, false, 1));
        return Util.permutation(cipherText, P, false, 1);
    }

    @Override
    public void setKeys(byte[] key) {
        feistelNetwork.setKeys(key);
    }

    @Override
    public int getBlockLength() {
        return feistelNetwork.getBlockLength();
    }
}