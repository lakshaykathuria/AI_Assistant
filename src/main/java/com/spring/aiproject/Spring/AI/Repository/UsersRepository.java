package com.spring.aiproject.Spring.AI.Repository;

import com.spring.aiproject.Spring.AI.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    Users findByUsername(String username);

}
