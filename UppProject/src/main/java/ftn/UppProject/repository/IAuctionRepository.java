package ftn.UppProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ftn.UppProject.entity.Auction;

@Repository
public interface IAuctionRepository extends JpaRepository<Auction, Long> {

}
