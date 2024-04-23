package ru.mai.khasanov.cryptowebmessenger.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mai.khasanov.cryptowebmessenger.Models.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByName(String roomName);
    List<Room> findAllByUsersId(Long userId);
}
