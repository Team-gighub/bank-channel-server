package com.bank.channel.baas.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    protected TestEntity() {
        // JPA 기본 생성자
    }

    public TestEntity(String value) {
        this.value = value;
    }
}
