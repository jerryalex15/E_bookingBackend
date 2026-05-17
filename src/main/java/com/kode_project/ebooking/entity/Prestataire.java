package com.kode_project.ebooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Prestataire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prestataireId;

    @OneToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String specialite;
    private String adresse;

    @ManyToOne
    @JoinColumn(name = "service_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Service service;

    @OneToMany(mappedBy = "prestataire", fetch = FetchType.LAZY)
    private List<RendezVous> rendezVousList;
}
