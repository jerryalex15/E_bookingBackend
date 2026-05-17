package com.kode_project.ebooking.entity;

import com.kode_project.ebooking.enums.Statut;
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
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rendezVousId;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "prestataire_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Prestataire prestataire;

    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Statut statut = Statut.EN_ATTENTE;
}
