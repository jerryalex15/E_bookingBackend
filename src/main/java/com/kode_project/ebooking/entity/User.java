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
    private String email;
    private String telephone;
    private String motDePasse;
    @Builder.Default
    private boolean activeStatut = true;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Prestataire prestataire;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RendezVous> rendezVousList;
}
