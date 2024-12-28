package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.entity.SuperInstance;
import br.com.blackhunter.bots.yui.manager.SuperInstanceManager;
import br.com.blackhunter.bots.yui.repository.SuperInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SuperInstanceService {
    @Autowired
    private SuperInstanceRepository superInstanceRepository;
    @Autowired
    private SuperInstanceManager superInstanceManager;

    public List<SuperInstance> getAllActiveInstances() {
        return superInstanceRepository.findAllByActiveTrue();
    }

    public void saveInstance(SuperInstance instance) {
        superInstanceRepository.save(instance);
    }

    public void deactivateInstance(UUID id) {
        superInstanceRepository.findById(id).ifPresent(instance -> {
            instance.setActive(false);
            superInstanceRepository.save(instance);
        });
    }

    public void startAllInstances() {
        List<SuperInstance> activeInstances = this.getAllActiveInstances();
        activeInstances.forEach(instance -> {
            superInstanceManager.startSuperInstance(instance);
        });
    }
}
