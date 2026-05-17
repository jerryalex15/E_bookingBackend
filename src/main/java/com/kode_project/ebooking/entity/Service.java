package com.kode_project.ebooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;
    private String nomService;
    @Column(nullable = true)
    private String description;

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<Prestataire> prestataires;
}