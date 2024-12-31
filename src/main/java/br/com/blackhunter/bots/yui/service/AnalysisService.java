package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.constants.ImageAIModel;
import br.com.blackhunter.bots.yui.dto.TrendingImageDTO;
import br.com.blackhunter.bots.yui.entity.PlatformIntegration;
import br.com.blackhunter.bots.yui.entity.SuperInstance;
import br.com.blackhunter.bots.yui.log.YuiLogger;
import br.com.blackhunter.bots.yui.util.PromptGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class AnalysisService {
    @Autowired
    private ShutterstockService shutterstockService;
    @Autowired
    private PlatformIntegrationService platformIntegrationService;
    @Autowired
    private CloudFlareImageAIService cloudFlareImageAIService;

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
            YuiLogger.info("[AnalysisService] - Iniciando o trend scrapping das plataformas.");
            // Aqui ele deve puxar as trends apenas das plataformas associadas ao usuário:
            for(PlatformIntegration plataform : this.plataforms) {
                switch (plataform.getPlataform()) {
                    case Shutterstock: {
                        YuiLogger.info("[AnalysisService] - Buscando tendências da Shutterstock...");
                        List<TrendingImageDTO> trendingShutterstockTags = shutterstockService.getTrendingTags(plataform);
                        generateOpportunities(trendingShutterstockTags, plataform);
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

    private void generateOpportunities(List<TrendingImageDTO> trendings, PlatformIntegration plataform) {
        // Gerar novas imagens baseadas nas tags
        YuiLogger.info("[] - @"+ plataform.getUserId().getName() + "/" + plataform.getPlataform() + ": Iniciando thread de oportunidade para uma lista com " + trendings.size() + " trendings.");
        AtomicInteger index = new AtomicInteger(0);
        trendings.stream().limit(10).forEach(trending -> createImageWithTag(index.getAndIncrement(), trending));
        YuiLogger.info("[] - @" + plataform.getUserId().getName() + "/" + plataform.getPlataform() + ": Thread de oportunidade finalizada!");
    }

    private void createImageWithTag(int index, TrendingImageDTO trending) {
        // Código para gerar imagem com IA com a tag específica
        String prompt = PromptGenerator.generatePrompt(trending);
        cloudFlareImageAIService.generate(ImageAIModel.FLUX_1_SCHNELL, index, prompt);
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
