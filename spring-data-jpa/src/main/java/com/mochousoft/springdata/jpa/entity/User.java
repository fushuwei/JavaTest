package com.mochousoft.springdata.jpa.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;
}
