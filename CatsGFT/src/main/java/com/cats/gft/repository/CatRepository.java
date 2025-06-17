package com.cats.gft.repository;

import com.cats.gft.model.CatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatRepository extends JpaRepository<CatModel, Long> {
    List<CatModel> findByBreed(String breed);
    List<CatModel> findByName(String name);
    List<CatModel> findByBreedAndName(String breed, String name);
    List<CatModel> findByImageUrlIsNotNull();
    List<CatModel> findByNameContaining(String keyword);
    List<CatModel> findByBreedContaining(String keyword);
}