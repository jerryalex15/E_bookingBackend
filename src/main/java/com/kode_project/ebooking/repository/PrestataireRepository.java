package com.kode_project.ebooking.repository;

import com.kode_project.ebooking.entity.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestataireRepository extends JpaRepository<Prestataire, Long>{
}