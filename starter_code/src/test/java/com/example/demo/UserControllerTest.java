package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        InjectorUtils.injectObjects(userController, "userRepository", userRepo);
        InjectorUtils.injectObjects(userController, "cartRepository", cartRepo);
        InjectorUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("testusername");
        user.setPassword("testpwd");
        user.setCart(cart);
        when(userRepo.findByUsername("testusername")).thenReturn(user);
        when(userRepo.findById(0L)).thenReturn(java.util.Optional.of(user));
        when(userRepo.findByUsername("someone")).thenReturn(null);
    }

    @Test
    public void createUserTest() {
        when(encoder.encode("testPwd")).thenReturn("thisIsHashedPwd");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testusername");
        createUserRequest.setPassword("testPwd");
        createUserRequest.setConfirmPassword("testPwd");
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("testusername", u.getUsername());
        assertEquals("thisIsHashedPwd", u.getPassword());
    }

    @Test
    public void createUserPasswordShortTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUsername");
        createUserRequest.setPassword("short");
        createUserRequest.setConfirmPassword("short");
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserPasswordMismatchTest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUsername");
        createUserRequest.setPassword("abcdefghj");
        createUserRequest.setConfirmPassword("abcdefghijkl");
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findUserByNameTest() {
        final ResponseEntity<User> response = userController.findByUserName("testusername");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals("testusername", u.getUsername());
    }

    @Test
    public void findUserNotFoundTest() {
        final ResponseEntity<User> response = userController.findByUserName("someone");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void findUserByIdTest() {
        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
    }

    @Test
    public void findUserByIdNotFoundTest() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
