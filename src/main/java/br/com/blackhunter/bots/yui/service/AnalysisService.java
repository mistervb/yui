package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.entity.PlatformIntegration;
import br.com.blackhunter.bots.yui.entity.SuperInstance;
import br.com.blackhunter.bots.yui.log.YuiLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AnalysisService {
    @Autowired
    private ShutterstockService shutterstockService;
    @Autowired
    private PlatformIntegrationService platformIntegrationService;

    List<PlatformIntegration> plataforms;

    public void startAnalysis(SuperInstance instance) {
        this.plataforms = List.of();
        try {
            if(instance.getUserId() == null) {
                errorLog("Usuário não associado a instância. Impossível continuar com a operação.");
                return;
            }

            this.plataforms = platformIntegrationService.findByUserId(instance.getUserId().getId());
        } catch (Exception e) {
            errorLog(e);
        }

        if(!this.plataforms.isEmpty()) {
            // Aqui ele deve puxar as trends apenas das plataformas associadas ao usuário:
            for(PlatformIntegration plataform : this.plataforms) {
                switch (plataform.getPlataform()) {
                    case Shutterstock: {
                        List<String> trendingShutterstockTags = shutterstockService.getTrendingTags(plataform);
                        generateOpportunities(trendingShutterstockTags);
                        break;
                    }
                    case Etsy:
                    case Adobe_Stock:
                    case Creative_Market: {
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("");
                    }
                }
            }

        }
    }

    private void generateOpportunities(List<String> trendingTags) {
        // Gerar novas imagens baseadas nas tags
        for (String tag : trendingTags) {
            // Chamar função de criação de imagem com IA
            createImageWithTag(tag);
        }
    }

    private void createImageWithTag(String tag) {
        // Código para gerar imagem com IA com a tag específica
        System.out.println("Criando imagem com a tag: " + tag);
    }

    private void errorLog(Exception e) {
        String msg = "[AnalysisService] - ERROR: " + e.getMessage();
        YuiLogger.error(msg);
        log.error(msg);
    }
    private void errorLog(String errMsg) {
        String msg = "[AnalysisService] - ERROR: " + errMsg;
        YuiLogger.error(msg);
        log.error(msg);
    }
}
