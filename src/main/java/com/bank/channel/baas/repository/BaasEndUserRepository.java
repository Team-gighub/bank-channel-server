package com.bank.channel.baas.repository;

import com.bank.channel.baas.domain.BaasEndUser;
import com.bank.channel.baas.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaasEndUserRepository extends JpaRepository<BaasEndUser, String> {

}
