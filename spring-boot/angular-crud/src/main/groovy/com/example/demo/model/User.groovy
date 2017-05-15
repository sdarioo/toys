package com.example.demo.model

import groovy.transform.Canonical

@Canonical
class User {
    Long id;
    String name;
    Integer age;
    double salary;
}
