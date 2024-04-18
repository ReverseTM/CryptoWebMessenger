package ru.mai.khasanov.cryptowebmessenger.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mai.khasanov.cryptowebmessenger.DTO.LeaveJoinRequest;
import ru.mai.khasanov.cryptowebmessenger.DTO.RoomRequest;
import ru.mai.khasanov.cryptowebmessenger.Models.Room;
import ru.mai.khasanov.cryptowebmessenger.Models.User;
import ru.mai.khasanov.cryptowebmessenger.Services.RoomService;
import ru.mai.khasanov.cryptowebmessenger.Services.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;
    private final UserService userService;

    @Autowired
    public RoomController(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomRequest roomRequest) {
        Optional<Room> existRoom = roomService.getRoomByName(roomRequest.getName());
        if (existRoom.isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Комната с таким названием уже существует");
        }

        Room room = roomService.createRoom(roomRequest);
        return ResponseEntity.ok().body(room);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Room>> getAllUserRooms(@PathVariable("userId") long userId) {
        List<Room> rooms = roomService.getRoomsByUserId(userId);
        return ResponseEntity.ok().body(rooms);
    }

    @PostMapping("/{roomName}/join")
    public ResponseEntity<String> joinRoom(@PathVariable String roomName, @RequestBody LeaveJoinRequest joinRequest) {
        long userId = joinRequest.getUserId();

        Optional<Room> optionalRoom = roomService.getRoomByName(roomName);
        if (optionalRoom.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Room room = optionalRoom.get();

        if (room.getUsers().size() >= 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("В комнате уже максимальное количество пользователей");
        }

        Optional<User> optionalUser = userService.getUserById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: Пользователь не найден");
        }

        User user = optionalUser.get();

        roomService.addUserToRoom(user, room);
        userService.addRoomToUser(user, room);

        return ResponseEntity.ok().body("Пользователь успешно подключен к комнате");
    }

    @DeleteMapping("/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(@PathVariable long roomId, @RequestBody LeaveJoinRequest leaveRequest) {
        long userId = leaveRequest.getUserId();

        Optional<Room> optionalRoom = roomService.getRoomById(roomId);
        if (optionalRoom.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Room room = optionalRoom.get();

        Optional<User> optionalUser = userService.getUserById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: Пользователь не найден");
        }

        User user = optionalUser.get();

        boolean removed = roomService.removeUserFromRoom(user, room);
        if (!removed) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: Пользователь не найден в комнате");
        }

        userService.removeRoomFromUser(user, room);

        if (room.getUsers().isEmpty()) {
            roomService.deleteRoom(roomId);
        }

        return ResponseEntity.ok().body("Пользователь успешно отключен от комнаты");
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable("roomId") long roomId) {
        Optional<Room> room = roomService.getRoomById(roomId);
        return room.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
