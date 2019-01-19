package ftn.UppProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ftn.UppProject.entity.Offer;

@Repository
public interface IOfferRepository extends JpaRepository<Offer, Long> {

	List<Offer> findByAuctionId(Long parseLong);

}
