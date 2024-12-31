package br.com.blackhunter.bots.yui;

import br.com.blackhunter.bots.yui.log.YuiLogger;
import br.com.blackhunter.bots.yui.seeder.DatabaseSeeder;
import br.com.blackhunter.bots.yui.service.SuperInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ApplicationStartupRunner {
    @Autowired
    private DatabaseSeeder databaseSeeder;
    @Autowired
    private SuperInstanceService superInstanceService;

    @EventListener(ApplicationReadyEvent.class)
    public void bootstrap() {
        System.out.println("\n");
        YuiLogger.info("© Victor Barberino - " + LocalDate.now().getYear() + ": Yui Money Bot - v1.0.0");
        /* Iniciar o plantio das sementes caso a aplicação esteja configurada para isso. */
        databaseSeeder.plant();

        /* Sobe as intâncias salvas pelos usuarios. */
        superInstanceService.startAllInstances();
    }
}
