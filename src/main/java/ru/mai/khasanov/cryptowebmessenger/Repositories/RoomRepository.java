package ru.mai.khasanov.cryptowebmessenger.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mai.khasanov.cryptowebmessenger.Models.Room;
import ru.mai.khasanov.cryptowebmessenger.Models.User;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByUsersId(Long userId);
}
