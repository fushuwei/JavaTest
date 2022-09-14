package com.mochousoft.springdata.jpa;

import com.mochousoft.springdata.jpa.entity.User;
import com.mochousoft.springdata.jpa.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@SpringBootTest
class SpringDataJpaApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Test
    void list() {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            System.out.println(user.getId());
            System.out.println(user.getUsername());
            System.out.println(user.getPassword() + "\n\n");
        }
    }

    @Test
    void page() {
        userRepository.findAll(PageRequest.of(1, 1)).forEach(System.out::println);
    }

}
