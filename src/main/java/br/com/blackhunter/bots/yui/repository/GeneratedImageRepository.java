package br.com.blackhunter.bots.yui.repository;

import br.com.blackhunter.bots.yui.entity.GeneratedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GeneratedImageRepository extends JpaRepository<GeneratedImage, UUID> {
}
