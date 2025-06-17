package com.dogs.gft.repository;

import com.dogs.gft.model.DogModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogRepository extends JpaRepository<DogModel, Long> {
    List<DogModel> findByBreed(String breed);
    List<DogModel> findByName(String name);
    List<DogModel> findByBreedAndName(String breed, String name);
    List<DogModel> findByImageUrlIsNotNull();
    List<DogModel> findByNameContaining(String keyword);
    List<DogModel> findByBreedContaining(String keyword);
}