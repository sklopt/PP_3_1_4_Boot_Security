package ru.kata.spring.boot_security.pp.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.pp.entities.Role;
import ru.kata.spring.boot_security.pp.entities.User;
import ru.kata.spring.boot_security.pp.repositories.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public void saveUser(User user) {
        entityManager.persist(user);
    }


    public void updateUser(User user) {
        entityManager.merge(user);
    }


    public void deleteUser(long id) {
        User someUser = entityManager.find(User.class, id);
        entityManager.remove(someUser);
    }


    public List<User> getAllUsers() {
        return entityManager.createQuery("select u from User u", User.class).getResultList();
    }


    public User getUserByEmail(String email) {
        return entityManager.createQuery("select u from User u where u.email =: email", User.class)
                .setParameter("login", email).getSingleResult();
    }


    public User getUserById(long id) {
        return entityManager.find(User.class, id);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByName(String username) {
        return userRepository.findUserByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        Optional<User> userPrimary = Optional.ofNullable(userRepository.findUserByUsername(username));
//        if (!userPrimary.isPresent()) {
//            throw new UsernameNotFoundException(username + " not found");
//        }
//        return (UserDetails) userPrimary.get();
//    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }
}
