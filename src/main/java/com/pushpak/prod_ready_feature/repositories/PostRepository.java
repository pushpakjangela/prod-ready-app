package com.pushpak.prod_ready_feature.repositories;

import com.pushpak.prod_ready_feature.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
