package com.microservices.employee.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

/**
 * JPA entity for the {@code territory} table (sales regions such as "South").
 */
@Entity
@Table(name = "territory")
@Data
public class TerritoryVo {

    /** Auto-increment primary key ({@code territory_id}). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "territory_id")
    private Long territoryId;

    /** Name, required and unique in the database. */
    @Column(name = "territory_name", nullable = false, unique = true)
    private String territoryName;

    /** Active flag; defaults to {@code true}, not used yet. */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /** Soft-delete flag; defaults to {@code false}. No endpoint sets it yet. */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /** Set once on insert. */
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    /** JPA callback run just before the insert: stamps {@link #createdAt}. */
    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }
}
