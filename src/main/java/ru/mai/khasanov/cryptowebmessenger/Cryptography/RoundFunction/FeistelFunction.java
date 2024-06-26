package ru.mai.khasanov.cryptowebmessenger.Cryptography.RoundFunction;


import ru.mai.khasanov.cryptowebmessenger.Cryptography.UtilFunctions.Util;
import ru.mai.khasanov.cryptowebmessenger.Cryptography.interfaces.IConvert;


public class FeistelFunction implements IConvert {
    public static final int[] E = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };

    public static final int[] P = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25,
    };

    @Override
    public byte[] convert(byte[] block, byte[] roundKey) {
        // Расширяющая перестановка
        byte[] extendedBlock = Util.permutation(block, E, false, 1);
        // XOR с раундовым ключом
        byte[] xoredBlock = Util.xor(extendedBlock, roundKey);
        // Преобразования с помощью S-box
        byte[] transformedBlock = Util.substitution(xoredBlock);
        // Конечная перестановка
        return Util.permutation(transformedBlock, P, false, 1);
    }
}
