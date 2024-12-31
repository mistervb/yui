package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.constants.ImageAIModel;
import br.com.blackhunter.bots.yui.dto.ImageProduct;
import br.com.blackhunter.bots.yui.dto.TrendingImageDTO;
import br.com.blackhunter.bots.yui.entity.PlatformIntegration;
import br.com.blackhunter.bots.yui.entity.SuperInstance;
import br.com.blackhunter.bots.yui.entity.User;
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
                        generateOpportunities(trendingShutterstockTags, plataform, shutterstockService);
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

    private void generateOpportunities(List<TrendingImageDTO> trendings, PlatformIntegration plataform, PlataformService plataformService) {
        // Gerar novas imagens baseadas nas tags
        YuiLogger.info("[AnalysisService] - @"+ plataform.getUserId().getName() + "/" + plataform.getPlataform() + ": Iniciando thread de oportunidade para uma lista com " + trendings.size() + " trendings.");

        AtomicInteger index = new AtomicInteger(0);
        /*List<ImageProduct> imageProducts = trendings.stream()
                .map(trending -> createImageProduct(index.getAndIncrement(), trending, plataform.getUserId())).toList();
        plataformService.startSalesThreads(imageProducts);
        */

        System.out.println("Quantidade de trendings: " + trendings.size());
        YuiLogger.info("[AnalysisService] - @" + plataform.getUserId().getName() + "/" + plataform.getPlataform() + ": Thread de oportunidade finalizada!");
    }

    private ImageProduct createImageProduct(int index, TrendingImageDTO trending, User user) {
        // Código para gerar imagem com IA com a tag específica
        ImageProduct imageProduct = new ImageProduct();
        String prompt = PromptGenerator.generatePrompt(trending);
        byte[] imageBytes = cloudFlareImageAIService.generate(ImageAIModel.FLUX_1_SCHNELL, index, prompt);

        imageProduct.setImageBytes(imageBytes);
        imageProduct.setDescription(trending.getDescription());
        imageProduct.setUser(user);
        imageProduct.setPrice(0.0d);
        return imageProduct;
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
