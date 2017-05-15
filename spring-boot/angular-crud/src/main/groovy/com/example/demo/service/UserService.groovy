package com.example.demo.service

import com.example.demo.model.User
import org.springframework.stereotype.Service

@Service
class UserService {

    private List<User> users = new ArrayList<>()

    User findById(long id) {
        users.find { it.id == id }
    }

    void saveUser(User user) {
        users << user
        user.id = users.size()
    }

    List<User> findAllUsers() {
       users
    }

    boolean isUserExist(User user) {
        user.id && findById(user.id)
    }

    void updateUser(User user) {
    }

    void deleteAllUsers() {
        users.clear()
    }

    void deleteUserById(long id) {
        users.removeAll { it.id == id }
    }
}
