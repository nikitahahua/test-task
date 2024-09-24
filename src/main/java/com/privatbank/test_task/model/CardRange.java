package com.privatbank.test_task.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "card_ranges")
public class CardRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @JsonProperty("bin")
    @Column(name = "bin", nullable = false)
    private int bin;

    @JsonProperty("min_range")
    @Column(name = "minRange", nullable = false)
    private BigInteger minRange;

    @JsonProperty("max_range")
    @Column(name = "maxRange", nullable = false)
    private BigInteger maxRange;

    @JsonProperty("alpha_code")
    @Column(name = "alphaCode")
    private String alphaCode;

    @JsonProperty("bank_name")
    @Column(name = "bankName")
    private String bankName;

    @ManyToOne
    @JoinColumn(name = "version_id", nullable = false)
    private Version version;

    public CardRange(Long id, int bin, BigInteger minRange, BigInteger maxRange, String alphaCode, String bankName, Version version) {
        this.id = id;
        this.bin = bin;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.alphaCode = alphaCode;
        this.bankName = bankName;
        this.version = version;
    }

    public CardRange(int bin, BigInteger minRange, BigInteger maxRange, String alphaCode, String bankName) {
        this.bin = bin;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.alphaCode = alphaCode;
        this.bankName = bankName;
    }

    public CardRange() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBin() {
        return bin;
    }

    public void setBin(int bin) {
        this.bin = bin;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public BigInteger getMinRange() {
        return minRange;
    }

    public void setMinRange(BigInteger minRange) {
        this.minRange = minRange;
    }

    public BigInteger getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(BigInteger maxRange) {
        this.maxRange = maxRange;
    }

    public String getAlphaCode() {
        return alphaCode;
    }

    public void setAlphaCode(String alphaCode) {
        this.alphaCode = alphaCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof CardRange cardRange)) return false;

        return id.equals(cardRange.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bin, minRange, maxRange, alphaCode, bankName, version);
    }

    @Override
    public String toString() {
        return String.format("CardRange[minRange=%s, maxRange=%s]", minRange, maxRange);
    }
}
