package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.util.HttpClientUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class CloudFlareImageAIService {
    @Value("${cloudflare.api.token}")
    private String token;
    @Value("${cloudflare.api.account-id}")
    private String accountId;

    @Autowired
    private HttpClientUtil httpClientUtil;

    public void generate(String prompt) {
        if (token == null || token.isEmpty() || accountId == null || accountId.isEmpty()) {
            throw new IllegalStateException("Token ou Account ID não configurados.");
        }

        String url = String.format(
                "https://api.cloudflare.com/client/v4/accounts/%s/ai/run/@cf/black-forest-labs/flux-1-schnell",
                accountId
        );
        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + token,
                "Accept", "application/json"
        );

        log.info("URL: {}", url);
        log.info("Prompt: {}", prompt);
        log.info("Headers: {}", headers);

        try {
            ResponseEntity<String> response = httpClientUtil.post(
                    url,
                    headers,
                    new GeneratePayload(prompt),
                    String.class
            );

            if (response == null) {
                throw new RuntimeException("Resposta nula recebida da API Cloudflare.");
            }

            log.info("Status Code: {}", response.getStatusCode());
            log.info("Response Headers: {}", response.getHeaders());

            if (response.getBody() == null) {
                throw new RuntimeException("Corpo da resposta está vazio.");
            }

            salvarImagem(response.getBody());
        } catch (Exception e) {
            log.error("Erro ao gerar imagem com o prompt '{}'", prompt, e);
        }
    }

    private void salvarImagem(String jsonResponse) {
        try {
            // Parse do JSON para extrair a string Base64
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            String base64Image = rootNode.path("result").path("image").asText();

            if (base64Image.isEmpty()) {
                throw new IllegalArgumentException("Conteúdo da imagem não encontrado na resposta.");
            }

            // Decodificar a string Base64 em bytes
            byte[] imagemBytes = Base64.getDecoder().decode(base64Image);

            // Salvar a imagem no disco
            BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(imagemBytes));
            File arquivoImagem = new File("imagem-gerada.png");
            ImageIO.write(imagem, "png", arquivoImagem);

            log.info("Imagem salva com sucesso em: {}", arquivoImagem.getAbsolutePath());
        } catch (Exception e) {
            log.error("Erro ao carregar ou salvar a imagem", e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GeneratePayload {
        String prompt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageGenerated {
        String type;
        String contentType;
        String format;
        String description;
    }
}
