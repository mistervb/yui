package br.com.blackhunter.bots.yui;

import br.com.blackhunter.bots.yui.seeder.DatabaseSeeder;
import br.com.blackhunter.bots.yui.service.SuperInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner {
    @Autowired
    private DatabaseSeeder databaseSeeder;
    @Autowired
    private SuperInstanceService superInstanceService;

    @EventListener(ApplicationReadyEvent.class)
    public void bootstrap() {
        /* Iniciar o plantio das sementes caso a aplicação esteja configurada para isso. */
        databaseSeeder.plant();

        /* Sobe as intâncias salvas pelos usuarios. */
        superInstanceService.startAllInstances();
    }
}
