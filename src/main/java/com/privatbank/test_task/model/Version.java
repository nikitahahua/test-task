package com.privatbank.test_task.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "versions")
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "version", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardRange> cardRanges;

    public Version(Long id, LocalDateTime createdAt, List<CardRange> cardRanges) {
        this.id = id;
        this.createdAt = createdAt;
        this.cardRanges = cardRanges;
    }

    public Version(LocalDateTime createdAt, List<CardRange> cardRanges) {
        this.createdAt = createdAt;
        this.cardRanges = cardRanges;
    }

    public Version() {
    }

    public Version(LocalDateTime now) {
        this.createdAt = now;
    }

    public List<CardRange> getCardRanges() {
        return cardRanges;
    }

    public void setCardRanges(List<CardRange> cardRanges) {
        this.cardRanges = cardRanges;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
