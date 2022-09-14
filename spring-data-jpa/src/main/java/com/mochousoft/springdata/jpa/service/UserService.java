package com.mochousoft.springdata.jpa.service;

import com.mochousoft.springdata.jpa.entity.User;
import com.mochousoft.springdata.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Page<User> getPage(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }
}
