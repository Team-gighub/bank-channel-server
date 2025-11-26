package com.bank.channel.baas.repository;

import com.bank.channel.baas.domain.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity, Long> {
}