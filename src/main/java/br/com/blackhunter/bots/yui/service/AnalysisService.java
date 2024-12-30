package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.dto.TrendingImageDTO;
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
        System.out.println("Oportunidade encontrada para a plataforma: " + plataform.getPlataform());
        /*for (TrendingImageDTO trending : trendings) {
            // Chamar função de criação de imagem com IA
            createImageWithTag(trending);
        }*/
        System.out.println("Total de trendings: " + trendings.size());
        trendings.stream().limit(2).forEach(this::createImageWithTag);
    }

    private void createImageWithTag(TrendingImageDTO trending) {
        // Código para gerar imagem com IA com a tag específica
        System.out.println("Tag: " + trending.getTags());
        System.out.println("Descrição: " + trending.getDescription());
        System.out.println("Categoria: " + trending.getCategory());
        System.out.println("Tipo de imagem: " + trending.getImageType());
        System.out.println("Tipo de mídia: " + trending.getMediaType());
        System.out.println("Resolução: " + trending.getResolution());
        System.out.println("Paletas de cores: " + trending.getColorPalette());
        System.out.println("Contexto de uso: " + trending.getIntendedUse());
        System.out.println("Nível de detalhamento: " + trending.getDetailLevel());
        System.out.println("Estilo artístico: " + trending.getStyle());
        System.out.println("Popularidade/Prioridade: " + trending.getPriority());
        System.out.println("Fonte de dados: " + trending.getSource());
        System.out.println("\n");
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
