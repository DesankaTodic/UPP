package ftn.UppProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ftn.UppProject.entity.Rate;

@Repository
public interface IRateRepository extends JpaRepository<Rate, Long> {

}