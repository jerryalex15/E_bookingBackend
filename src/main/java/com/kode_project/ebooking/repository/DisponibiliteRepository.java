package com.kode_project.ebooking.repository;

import com.kode_project.ebooking.entity.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
    List<Disponibilite> findByPrestatairePrestataireId(Long prestataireId);

    @Query("SELECT COUNT(d) FROM Disponibilite d")
    long countTotalDisponibilites();

    @Query("SELECT d FROM Disponibilite d " +
            "WHERE d.prestataire.prestataireId = :prestataireId " +
            "AND d.date = :date " +
            "AND d.heureDebut <= :heureDebut " +
            "AND d.heureFin >= :heureFin")
    List<Disponibilite> findPrestataireDisponibilities(
            @Param("prestataireId") Long prestataireId,
            @Param("date") LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin
    );
}
