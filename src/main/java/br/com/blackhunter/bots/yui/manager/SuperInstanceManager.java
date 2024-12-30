package br.com.blackhunter.bots.yui.manager;

import br.com.blackhunter.bots.yui.entity.SuperInstance;
import br.com.blackhunter.bots.yui.log.YuiLogger;
import br.com.blackhunter.bots.yui.service.AnalysisService;
import br.com.blackhunter.bots.yui.service.CloudFlareImageAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SuperInstanceManager {
    @Autowired
    private AnalysisService analysisService;
    @Autowired
    private CloudFlareImageAIService cloudFlareImageAIService;

    public void startSuperInstance(SuperInstance instance) {
        YuiLogger.info("[Super Instance Manager] - Iniciando Super Instância: " + instance.getName());
        Thread analysisThread = new Thread(() -> {
            YuiLogger.info("[Super Instance Manager] - @" + instance.getUserId().getName() + " - Thread de Análise iniciada para: " + instance.getName());
            // Código da lógica de análise e threads de oportunidades

            // Thread de análise:
            //analysisService.startAnalysis(instance);
            cloudFlareImageAIService.generate("Create a vector-style line art illustration of a spring floral background with pink flowers and golden texture. The design should be elegant, with gold blossoms, perfect for fabric prints or wallpaper. The image should evoke a luxury feel and be suitable for general use. The colors should primarily focus on pink and gold with soft accents.");
        });
        analysisThread.start();
        // Opcional: Salvar a referência da thread no banco, se necessário.
    }
}
