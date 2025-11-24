package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);
    
    User updateUser(User user);
    
    List<User> getUsersList();

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    List<User> getFriends(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer otherUserId);
    
    Optional<User> findUserById(Integer id);
}
