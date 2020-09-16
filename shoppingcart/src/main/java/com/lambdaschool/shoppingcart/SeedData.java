package com.lambdaschool.shoppingcart;

import com.lambdaschool.shoppingcart.models.Role;
import com.lambdaschool.shoppingcart.models.User;
import com.lambdaschool.shoppingcart.models.UserRole;
import com.lambdaschool.shoppingcart.repositories.CartRepository;
import com.lambdaschool.shoppingcart.repositories.ProductRepository;
import com.lambdaschool.shoppingcart.repositories.RoleRepository;
import com.lambdaschool.shoppingcart.repositories.UserRepository;
import com.lambdaschool.shoppingcart.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Transactional
@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    RoleService roleService;

    @Transactional

    @Override
    public void run(String... args) throws Exception {

        User u01 = new User("barnbarn", "", "werd");

        Role admin = new Role("ADMIN");
        Role user = new Role("USER");

        roleRepository.save(admin);
        roleRepository.save(user);

        u01.getRoles().add(new UserRole(roleService.findByName("ADMIN"), u01));

        userRepository.save(u01);
    }
}
