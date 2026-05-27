package com.kode_project.ebooking.repository;

import com.kode_project.ebooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT COUNT(u) FROM User u WHERE u.activeStatut = true")
    long countUtilisateursActifs();
    User findByEmail(String email);
}
