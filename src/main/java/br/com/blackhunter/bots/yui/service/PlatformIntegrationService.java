package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.entity.PlatformIntegration;
import br.com.blackhunter.bots.yui.entity.User;
import br.com.blackhunter.bots.yui.repository.PlataformIntegrationRepository;
import br.com.blackhunter.bots.yui.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PlatformIntegrationService {
    @Autowired
    private PlataformIntegrationRepository platformIntegrationRepository;
    @Autowired
    private UserRepository userRepository;

    public List<PlatformIntegration> findAll() {
        return platformIntegrationRepository.findAll();
    }

    public PlatformIntegration findById(UUID id) {
        return platformIntegrationRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Plataforma não encontrada com o ID: " + id));
    }

    public PlatformIntegration create(PlatformIntegration platformIntegration) {
        if (platformIntegration.getId() != null && platformIntegrationRepository.existsById(platformIntegration.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A plataforma com este ID já existe.");
        }
        return platformIntegrationRepository.save(platformIntegration);
    }

    public PlatformIntegration update(UUID id, PlatformIntegration updatedPlatformIntegration) {
        PlatformIntegration existingIntegration = findById(id);
        existingIntegration.setUserId(updatedPlatformIntegration.getUserId());
        existingIntegration.setPlataform(updatedPlatformIntegration.getPlataform());
        existingIntegration.setApiKey(updatedPlatformIntegration.getApiKey());
        existingIntegration.setApiSecret(updatedPlatformIntegration.getApiSecret());
        existingIntegration.setCreatedAt(updatedPlatformIntegration.getCreatedAt());
        return platformIntegrationRepository.save(existingIntegration);
    }

    public void delete(UUID id) {
        if (!platformIntegrationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plataforma não encontrada com o ID: " + id);
        }
        platformIntegrationRepository.deleteById(id);
    }

    public List<PlatformIntegration> findByUserId(UUID userId) throws NoPlatformsAssociatedException {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com o ID: " + userId));
        List<PlatformIntegration> platforms = platformIntegrationRepository.findAllByUserId(user);
        if (platforms.isEmpty()) {
            throw new NoPlatformsAssociatedException("O usuário com o ID: " + userId + " não possui plataformas associada a essa conta. Impossível de prosseguir com a operação.");
        }
        return platforms;
    }

    public static class NoPlatformsAssociatedException extends Exception {
        public NoPlatformsAssociatedException(String message) {
            super(message);
        }
    }
}
