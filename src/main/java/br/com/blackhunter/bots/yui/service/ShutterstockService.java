package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.entity.PlatformIntegration;
import br.com.blackhunter.bots.yui.log.YuiLogger;
import br.com.blackhunter.bots.yui.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShutterstockService implements PlataformService {
    @Autowired
    private HttpClientUtil httpClientUtil;

    @Override
    public List<String> getTrendingTags(PlatformIntegration plataform) {
        String url = "https://api.shutterstock.com/v2/images/search";
        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + plataform.getApiKey(),
                "Accept", "application/json"
        );
        System.out.println(plataform.getApiKey());
        try {
            // Faz a requisição e obtém a resposta
            ResponseEntity<ShutterstockResponse> response = httpClientUtil.get(url + "?sort=popular&per_page=10", headers, ShutterstockResponse.class);

            // Extrai as tags das imagens retornadas
            return response.getBody().getData().stream()
                    .flatMap(image -> image.getKeywords().stream())
                    .distinct()
                    .limit(10)  // Pega até 10 tags distintas
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Loga o erro e retorna uma lista vazia
            String msg = "[ShutterstockService] - ERROR: Erro ao obter tendências do Shutterstock: " + e.getMessage();
            System.err.println(msg);
            YuiLogger.error(msg);
            return List.of();
        }
    }

    // Classe para mapear a resposta da API do Shutterstock
    public static class ShutterstockResponse {
        private List<ImageData> data;

        public List<ImageData> getData() {
            return data;
        }

        public void setData(List<ImageData> data) {
            this.data = data;
        }
    }

    public static class ImageData {
        private List<String> keywords;

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }
    }
}