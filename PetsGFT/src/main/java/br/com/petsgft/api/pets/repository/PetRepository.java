package br.com.petsgft.api.pets.repository;

import br.com.petsgft.api.pets.model.PetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PetRepository extends JpaRepository<PetModel, Integer> {
}