package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        
        log.info("User {} added friend {}", userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        
        log.info("User {} removed friend {}", userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        User user = userStorage.getUserById(userId);
        List<User> friends = new ArrayList<>();
        
        for (Integer friendId : user.getFriends()) {
            friends.add(userStorage.getUserById(friendId));
        }
        
        log.info("Getting friends list for user {}: count={}", userId, friends.size());
        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = userStorage.getUserById(userId);
        User other = userStorage.getUserById(otherId);
        
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherFriends = other.getFriends();
        
        Set<Integer> commonFriendIds = userFriends.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toSet());
        
        List<User> commonFriends = new ArrayList<>();
        for (Integer friendId : commonFriendIds) {
            commonFriends.add(userStorage.getUserById(friendId));
        }
        
        log.info("Getting common friends for users {} and {}: count={}", userId, otherId, commonFriends.size());
        return commonFriends;
    }
}

