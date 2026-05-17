package com.kode_project.ebooking.repository;

import com.kode_project.ebooking.entity.RendezVous;
import com.kode_project.ebooking.enums.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    List<RendezVous> findByUserUserId(Long id);

    List<RendezVous> findByPrestatairePrestataireId(Long id);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.statut = :statut")
    long countByStatut(@Param("statut") Statut statut);

    @Query("SELECT r FROM RendezVous r " +
            "WHERE r.prestataire.prestataireId = :prestataireId " +
            "AND r.date = :date " +
            "AND r.heureDebut < :heureFin " +
            "AND r.heureFin > :heureDebut " +
            "AND r.statut != 'ANNULE'")
    List<RendezVous> findOverlappingRendezVous(
            @Param("prestataireId") Long prestataireId,
            @Param("date") LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin
    );

    @Query("SELECT r FROM RendezVous r " +
            "WHERE r.prestataire.prestataireId = :prestataireId " +
            "AND r.date = :date " +
            "AND r.heureDebut < :heureFin " +
            "AND r.heureFin > :heureDebut " +
            "AND r.statut != 'ANNULE' " +
            "AND r.rendezVousId != :excludeId")
    List<RendezVous> findOverlappingRendezVousExcluding(
            @Param("prestataireId") Long prestataireId,
            @Param("date") LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("excludeId") Long excludeId
    );
}
