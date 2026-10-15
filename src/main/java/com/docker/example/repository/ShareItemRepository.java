package com.docker.example.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docker.example.model.ShareItem;

public interface ShareItemRepository extends JpaRepository<ShareItem, Long>{
		
	List<ShareItem> findByExpiresAtBefore(Instant time);

	 Optional<ShareItem> findByToken(String token);

}
