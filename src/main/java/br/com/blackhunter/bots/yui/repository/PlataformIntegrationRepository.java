package br.com.blackhunter.bots.yui.repository;

import br.com.blackhunter.bots.yui.entity.PlatformIntegration;
import br.com.blackhunter.bots.yui.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlataformIntegrationRepository extends JpaRepository<PlatformIntegration, UUID> {
    List<PlatformIntegration> findAllByUserId(User user);
}
