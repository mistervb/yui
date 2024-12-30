package br.com.blackhunter.bots.yui.seeder;

import br.com.blackhunter.bots.yui.config.PlataformControl;
import br.com.blackhunter.bots.yui.constants.InstanceStatus;
import br.com.blackhunter.bots.yui.constants.Plataform;
import br.com.blackhunter.bots.yui.entity.PlatformIntegration;
import br.com.blackhunter.bots.yui.entity.SuperInstance;
import br.com.blackhunter.bots.yui.entity.User;
import br.com.blackhunter.bots.yui.log.YuiLogger;
import br.com.blackhunter.bots.yui.repository.PlataformIntegrationRepository;
import br.com.blackhunter.bots.yui.repository.SuperInstanceRepository;
import br.com.blackhunter.bots.yui.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DatabaseSeeder {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlataformIntegrationRepository plataformIntegrationRepository;
    @Autowired
    private SuperInstanceRepository superInstanceRepository;
    @Autowired
    private PlataformControl plataformControl;

    @Value("${mock.owner.pix-key}")
    private String pixKey;
    @Value("${mock.mock-mode}")
    private String mockMode;

    public void plant() {
        YuiLogger.info("[Database Seeder] - Ready.");
        if(mockMode.equals("true")) {
            LocalTime start = LocalTime.now();
            YuiLogger.info("[Database Seeder] - Iniciando carga no banco.");

            User mockOwner = userRepository.save(makeOwner());
            List<PlatformIntegration> plataforms = plataformIntegrationRepository.saveAll(makeAllPlataforms(mockOwner));
            List<SuperInstance> instances = superInstanceRepository.saveAll(makeThreeSuperInstance(mockOwner));

            LocalTime end = LocalTime.now();
            int tempo = end.getMinute() - start.getMinute();
            YuiLogger.info("[Database Seeder] - Carga no banco finalizada em " + tempo + (tempo > 1 ? " minutos." : " minuto." ));
        }
    }

    private User makeOwner() {
        User mockOwner = new User();
        mockOwner.setName("Victor Barberino");
        mockOwner.setLastLogin(LocalDateTime.now());
        mockOwner.setPixKey(pixKey);
        mockOwner.setEmail("vitu.barberino@gmail.com");
        return mockOwner;
    }

    private List<SuperInstance> makeThreeSuperInstance(User mockUser) {
        List<SuperInstance> instances = new ArrayList<>();
        for(int i = 0; i < 1; i ++) {
            SuperInstance instance = new SuperInstance();
            InetAddress netLocalHost = null;
            String localHost = "";
            try {
                netLocalHost = InetAddress.getLocalHost();
                localHost = netLocalHost.getHostName() + "//" + netLocalHost.getHostAddress();
            }
            catch (Exception e) {
                YuiLogger.error("[Database Seeder] - ERROR: Ocorreu um erro inesperado ao pegar os dados do localhost.");
                YuiLogger.warn("[Database Seeder] - Atenção: O nome da instancia ficará sem os dados do host.");
            }

            instance.setName("Sup_" + (i + 1) + "_" + LocalDate.now() + "_" + LocalTime.now() + "_" + localHost);
            instance.setStatus(InstanceStatus.ATIVA);
            instance.setActive(true);
            instance.setAnalysisThreadId("SupID-" + UUID.randomUUID().toString());
            instance.setUserId(mockUser);
            instance.setCreatedAt(LocalDateTime.now());

            instances.add(instance);
        }

        return instances;
    }

    private List<PlatformIntegration> makeAllPlataforms(User user) {
        List<PlatformIntegration> list = new ArrayList<>();
        for(Plataform p : plataformControl.getAllPlataforms()) {
            PlatformIntegration integration = new PlatformIntegration();
            PlataformControl.ApiData apiData = plataformControl.getApiData(p);
            integration.setPlataform(p);
            integration.setApiKey(apiData.getKey());
            integration.setApiSecret(apiData.getSecret());
            integration.setCreatedAt(LocalDateTime.now());
            integration.setUserId(user);
            list.add(integration);
        }

        return list;
    }
}
