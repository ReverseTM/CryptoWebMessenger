package ru.mai.khasanov.cryptowebmessenger.Cryptography.FeistelNetwork;


import ru.mai.khasanov.cryptowebmessenger.Cryptography.UtilFunctions.Util;
import ru.mai.khasanov.cryptowebmessenger.Cryptography.interfaces.IConvert;
import ru.mai.khasanov.cryptowebmessenger.Cryptography.interfaces.IEncryptor;
import ru.mai.khasanov.cryptowebmessenger.Cryptography.interfaces.IKeyExpand;

public class FeistelNetwork implements IEncryptor {
    private final IKeyExpand keyExtend;
    private final IConvert feistelFunction;
    protected int rounds;
    private byte[][] roundKeys;

    public FeistelNetwork(IKeyExpand keyExtend, IConvert feistelFunction, int rounds) {
        this.keyExtend = keyExtend;
        this.feistelFunction = feistelFunction;
        this.rounds = rounds;
    }

    @Override
    public byte[] encode(byte[] data) {
        // Делим входной блок на 2 блока
        var half = data.length / 2;
        byte[] L = new byte[half], R = new byte[half];

        System.arraycopy(data, 0, L, 0, half);
        System.arraycopy(data, half, R, 0, half);

        // Выполняем 16 раундов шифрования
        for (int i = 0; i < rounds - 1; ++i) {
            byte[] tmp = Util.xor(L, feistelFunction.convert(R, roundKeys[i]));
            L = R;
            R = tmp;
        }
        L = Util.xor(L, feistelFunction.convert(R, roundKeys[rounds - 1]));

        byte[] result = new byte[data.length];

        System.arraycopy(L, 0, result, 0, half);
        System.arraycopy(R, 0, result, half, half);

        return result;
    }

    @Override
    public byte[] decode(byte[] data) {
        // Делим входной блок на 2 блока
        var half = data.length / 2;
        byte[] L = new byte[half], R = new byte[half];

        System.arraycopy(data, 0, L, 0, half);
        System.arraycopy(data, half, R, 0, half);

        // Выполняем 16 раундов дешифрования
        L = Util.xor(L, feistelFunction.convert(R, roundKeys[rounds - 1]));
        for (int i = rounds - 2; i >= 0; --i) {
            byte[] tmp = Util.xor(R, feistelFunction.convert(L, roundKeys[i]));
            R = L;
            L = tmp;
        }

        byte[] result = new byte[data.length];

        System.arraycopy(L, 0, result, 0, half);
        System.arraycopy(R, 0, result, half, half);

        return result;
    }

    @Override
    public void setKeys(byte[] key) {
        roundKeys = keyExtend.genKeys(key);
    }

    @Override
    public int getBlockLength() {
        return 8;
    }
}