package br.com.blackhunter.bots.yui.repository;

import br.com.blackhunter.bots.yui.entity.SuperInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuperInstanceRepository extends JpaRepository<SuperInstance, UUID> {
    List<SuperInstance> findAllByActiveTrue();
}
