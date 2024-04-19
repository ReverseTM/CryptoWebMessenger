package ru.mai.khasanov.cryptowebmessenger.Cryptography.interfaces;


// По сути функция фейстеля
public interface IConvert {

    byte[] convert(byte[] block, byte[] roundKey);
}
