package com.kode_project.ebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kode_project.ebooking.entity.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
