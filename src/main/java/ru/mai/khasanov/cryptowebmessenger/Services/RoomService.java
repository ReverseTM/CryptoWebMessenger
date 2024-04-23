package ru.mai.khasanov.cryptowebmessenger.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mai.khasanov.cryptowebmessenger.DTO.RoomRequest;
import ru.mai.khasanov.cryptowebmessenger.Models.Room;
import ru.mai.khasanov.cryptowebmessenger.Models.User;
import ru.mai.khasanov.cryptowebmessenger.Repositories.RoomRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }
    public Room createRoom(RoomRequest roomRequest) {
        Room room = new Room();
        room.setName(roomRequest.getName());
        room.setEncryptionAlgorithm(roomRequest.getEncryptionAlgorithm());
        room.setCipherMode(roomRequest.getCipherMode());
        room.setPaddingMode(roomRequest.getPaddingMode());
        return roomRepository.save(room);
    }

    public void addUserToRoom(User user, Room room) {
        room.getUsers().add(user);
        roomRepository.save(room);
    }

    public Optional<Room> getRoomById(Long roomId) {
        return roomRepository.findById(roomId);
    }

    public Optional<Room> getRoomByName(String roomName) {
        return roomRepository.findByName(roomName);
    }

    public List<Room> getRoomsByUserId(Long userId) {
        return roomRepository.findAllByUsersId(userId);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public boolean removeUserFromRoom(User user, Room room) {
        if (room.getUsers().contains(user)) {
            room.getUsers().remove(user);
            roomRepository.save(room);
            return true;
        }
        return false;
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
