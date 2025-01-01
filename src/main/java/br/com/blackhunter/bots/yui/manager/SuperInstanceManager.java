package br.com.blackhunter.bots.yui.manager;

import br.com.blackhunter.bots.yui.entity.SuperInstance;
import br.com.blackhunter.bots.yui.log.YuiLogger;
import br.com.blackhunter.bots.yui.service.TrendScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SuperInstanceManager {
    @Autowired
    private TrendScrapingService trendScrapingService;

    public void startSuperInstance(SuperInstance instance) {
        YuiLogger.info("[Super Instance Manager] - Iniciando Super Instância: " + instance.getName());
        Thread analysisThread = new Thread(() -> {
            YuiLogger.info("[Super Instance Manager] - @" + instance.getUserId().getName() + " - Thread de Análise iniciada para: " + instance.getName());
            // Código da lógica de análise e threads de oportunidades

            // Thread de análise:
            trendScrapingService.startScraping(instance);
        });
        analysisThread.start();
        // Opcional: Salvar a referência da thread no banco, se necessário.
    }
}
