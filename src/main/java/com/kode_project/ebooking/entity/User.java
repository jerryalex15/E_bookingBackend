package com.kode_project.ebooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String prenom;
    private String nom;
    @Column(unique = true)
    private String email;
    private String telephone;
    private String motDePasse;
    @Builder.Default
    private boolean activeStatut = true;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Prestataire prestataire;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RendezVous> rendezVousList;
}
