package com.kode_project.ebooking.entity;

import com.kode_project.ebooking.enums.JourSemaine;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Disponibilite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long disponibiliteId;

    @ManyToOne
    @JoinColumn(name = "prestataire_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Prestataire prestataire;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private JourSemaine jourSemaine;
    private LocalTime heureDebut;
    private LocalTime heureFin;
}
