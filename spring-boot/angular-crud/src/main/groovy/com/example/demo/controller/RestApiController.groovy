package com.example.demo.controller

import com.example.demo.error.CustomErrorType
import com.example.demo.model.User
import com.example.demo.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api")
class RestApiController {

    static final Logger logger = LoggerFactory.getLogger(RestApiController.class)

    UserService userService

    RestApiController(UserService userService) {
        this.userService = userService
    }

    @GetMapping(value = "/user/")
    ResponseEntity<List<User>> listAllUsers() {
        def users = userService.findAllUsers()
        if (!users) {
            return new ResponseEntity(HttpStatus.NO_CONTENT)
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK)
    }

    // -------------------Retrieve Single User------------------------------------------

    @GetMapping(value = "/user/{id}")
    ResponseEntity<?> getUser(@PathVariable("id") long id) {
        logger.info("Fetching User with id {}", id)
        User user = userService.findById(id)
        if (user == null) {
            logger.error("User with id {} not found.", id)
            return new ResponseEntity(new CustomErrorType("User with id " + id
                    + " not found"), HttpStatus.NOT_FOUND)
        }
        return new ResponseEntity<User>(user, HttpStatus.OK)
    }

    // -------------------Create a User-------------------------------------------

    @PostMapping(value = "/user/")
    ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        logger.info("Creating User : {}", user)

        if (userService.isUserExist(user)) {
            logger.error("Unable to create. A User with name {} already exist", user.getName())
            return new ResponseEntity(new CustomErrorType("Unable to create. A User with name " +
                    user.getName() + " already exist."),HttpStatus.CONFLICT)
        }
        userService.saveUser(user)

        def headers = new HttpHeaders()
        headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri())
        return new ResponseEntity<String>(headers, HttpStatus.CREATED)
    }

    // ------------------- Update a User ------------------------------------------------

    @PutMapping(value = "/user/{id}")
    ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        logger.info("Updating User with id {}", id)

        User currentUser = userService.findById(id)

        if (!currentUser) {
            logger.error("Unable to update. User with id {} not found.", id)
            return new ResponseEntity(new CustomErrorType("Unable to upate. User with id " + id + " not found."),
                    HttpStatus.NOT_FOUND)
        }

        currentUser.setName(user.getName())
        currentUser.setAge(user.getAge())
        currentUser.setSalary(user.getSalary())

        userService.updateUser(currentUser)
        return new ResponseEntity<User>(currentUser, HttpStatus.OK)
    }

    // ------------------- Delete a User-----------------------------------------

    @DeleteMapping(value = "/user/{id}")
    ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        logger.info("Fetching & Deleting User with id {}", id)

        User user = userService.findById(id)
        if (!user) {
            logger.error("Unable to delete. User with id {} not found.", id)
            return new ResponseEntity(new CustomErrorType("Unable to delete. User with id " + id + " not found."),
                    HttpStatus.NOT_FOUND)
        }
        userService.deleteUserById(id)
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT)
    }

    // ------------------- Delete All Users-----------------------------

    @DeleteMapping(value = "/user/")
    ResponseEntity<User> deleteAllUsers() {
        logger.info("Deleting All Users")

        userService.deleteAllUsers()
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT)
    }
}
