package ru.mai.khasanov.cryptowebmessenger.Cryptography.UtilFunctions;

public class Util {
    public static final int[][] S = {
            // S1
            {
                    14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                    0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                    4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                    15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13,
            },
            // S2
            {
                    15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                    3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                    0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                    13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
            },
            // S3
            {
                    10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                    13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                    13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                    1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
            },
            // S4
            {
                    7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                    13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                    10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                    3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14,
            },
            // S5
            {
                    2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                    14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                    4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                    11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
            },
            // S6
            {
                    12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                    10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                    9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                    4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
            },
            // S7
            {
                    4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                    13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                    1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                    6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
            },
            // S8
            {
                    13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                    1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
                    7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
                    2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
            }
    };

    public static byte[] permutation(
            byte[] block,
            int[] PBlock,
            boolean reverseOrder,
            int indexing) {
        if (block == null || PBlock == null) {
            throw new RuntimeException("Null pointer encountered");
        }
        if (indexing < 0 || indexing > 1) {
            throw new RuntimeException("Indexing out of bounds");
        }

        byte[] result = new byte[(PBlock.length + 7) / 8];
        int currentIndex = 0;

        for (var i : PBlock) {
            // учитываем с какого номера начинается индексация
            int pos = i - indexing;

            // находим смещение, учитывая как нумеруются биты
            int bitOffset = reverseOrder ? pos % 8 : 7 - pos % 8;
            int resultOffset = 7 - currentIndex % 8;

            // находим нужный индекс, учитывая нумерацию битов
            int blockIndex = reverseOrder ? block.length - 1 - pos / 8 : pos / 8;
            int resultIndex = currentIndex / 8;

            boolean value = (block[blockIndex] & (1 << bitOffset)) != 0;

            result[resultIndex] = (byte) (value
                    ? result[resultIndex] | (1 << resultOffset)
                    : result[resultIndex] & ~(1 << resultOffset));

            currentIndex++;
        }

        return result;
    }

    public static byte[] substitution(byte[] block) {
        if (block == null) {
            throw new RuntimeException("Null pointer encountered");
        }

        byte[] result = {0, 0, 0, 0};

        // Преобразовываем массив из 6 байтов в long
        long longBlock = 0;
        for (int i = 0; i < 6; ++i) {
            longBlock = (longBlock << 8) | (block[i] & 0xFF);
        }

        // Заменяем значениями из S блоков
        for (int i = 0; i < 8; i++) {
            // Достаём 6 бит
            int bits = (int) ((longBlock >> ((7 - i) * 6)) & 0xFF);
            int[] bitsArray = new int[6];

            // Достаём значение каждого бита
            for (int j = 0; j < 6; ++j) {
                bitsArray[j] = (bits >> (5 - j)) & 1;
            }

            // Находим номер строки и столбца
            int row = (bitsArray[0] << 1) | bitsArray[5];
            int col = (bitsArray[1] << 3)
                    | (bitsArray[2] << 2)
                    | (bitsArray[3] << 1)
                    | (bitsArray[4]);

            int value = S[i][row * 16 + col];

            result[i / 2] |= (byte) ((i & 1) == 0
                    ? value << 4
                    : value);
        }
        return result;
    }

    public static byte[] xor(byte[] x, byte[] y) {
        var size = Math.min(x.length, y.length);

        var result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) (x[i] ^ y[i]);
        }
        return result;
    }

    public static int leftCycleShift(int num, int numBits, int shiftAmount) {
        // Создаём маску для выделения (numBits) младших битов
        int mask = (1 << numBits) - 1;
        // Циклический сдвиг влево
        return (((num & mask) << shiftAmount) | (num >>> (numBits - shiftAmount))) & mask;
    }

    public static int rightCycleShift(int num, int numBits, int shiftAmount) {
        // Создаём маску для выделения (numBits) младших битов
        int mask = (1 << numBits) - 1;
        // Циклический сдвиг вправо
        return (num >>> shiftAmount) | ((num << (numBits - shiftAmount)) & mask);
    }
}
