package ru.mai.khasanov.cryptowebmessenger.Cryptography.ExpandKey;

import ru.mai.khasanov.cryptowebmessenger.Cryptography.UtilFunctions.Util;
import ru.mai.khasanov.cryptowebmessenger.Cryptography.interfaces.IKeyExpand;

public class DESKeyExpand implements IKeyExpand {

    public static final int[] PC_1 = {
            57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4
    };

    public static final int[] PC_2 = {
            14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
    };

    public static final int[] SHIFTS = {
            1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };
    private final int rounds;

    public DESKeyExpand(int rounds) {
        this.rounds = rounds;
    }
    @Override
    public byte[][] genKeys(byte[] key) {
//        int countUnit = 0;
//        for (int i = 0; i < key.length * 8; ++i) {
//            if (((key[i / 8] >>> (i % 8)) & 1) == 1) {
//                countUnit++;
//            }
//            if ((i + 1) % 8 == 0) {
//                if ((countUnit & 1) == 0) {
//                    throw new RuntimeException("Invalid key");
//                }
//                countUnit = 0;
//            }
//        }

        byte[][] keys = new byte[rounds][];

        // Сжимающая перестановка
        byte[] pKey = Util.permutation(key, PC_1, false, 1);

        // Делим ключ на 2 блока по 28 бит
        int C = ((pKey[0] & 0xFF) << 20)
                | ((pKey[1] & 0xFF) << 12)
                | ((pKey[2] & 0xFF) << 4)
                | ((pKey[3] & 0xFF) >>> 4);

        int D = ((pKey[3] & 0x0F) << 24)
                | ((pKey[4] & 0xFF) << 16)
                | ((pKey[5] & 0xFF) << 8)
                | ((pKey[6] & 0xFF));

        // Генерируем раундовые ключи
        for (int i = 0; i < rounds; ++i) {
            // Делаем циклические сдвиги
            C = Util.leftCycleShift(C, 28, SHIFTS[i]);
            D = Util.leftCycleShift(D, 28, SHIFTS[i]);

            // Склеиваем два блока в 1 блок
            long CD = ((long) C) << 28 | D;

            byte[] byteCD = new byte[7];
            for (int j = 0; j < 7; ++j) {
                byteCD[j] = (byte) ((CD >>> ((6 - j) * 8)) & 0xFF);
            }

            // Перестановка
            keys[i] = Util.permutation(byteCD, PC_2, false, 1);
        }

        return keys;
    }
}
