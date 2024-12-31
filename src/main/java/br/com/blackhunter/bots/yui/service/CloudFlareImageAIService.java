package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.constants.ImageAIModel;
import br.com.blackhunter.bots.yui.log.YuiLogger;
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
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CloudFlareImageAIService {
    @Value("${cloudflare.api.token}")
    private String token;
    @Value("${cloudflare.api.account-id}")
    private String accountId;

    @Autowired
    private HttpClientUtil httpClientUtil;

    public byte[] generate(ImageAIModel model, int index, String prompt) {
        if (token == null || token.isEmpty() || accountId == null || accountId.isEmpty()) {
            throw new IllegalStateException("Token ou Account ID não configurados.");
        }

        switch (model) {
            case FLUX_1_SCHNELL: {
                return generateFlux1Schnell(model, index, prompt);
            }
            default: {
                String msg = "[CloudFlareImageAIService] - ERROR: Modelo de IA não suportada nesta versão: " + model.getModelName();
                YuiLogger.error(msg);
                throw new IllegalArgumentException(msg);
            }
        }
    }

    private byte[] generateFlux1Schnell(ImageAIModel model, int index, String prompt) {
        String url = String.format(
                "https://api.cloudflare.com/client/v4/accounts/%s/%s",
                accountId, model.getEndpoint()
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

            return salvarImagem(index, response.getBody());
        } catch (Exception e) {
            String msg = "[CloudFlareImageAIService] - ERROR: " + e.getMessage();
            log.error(msg);
            YuiLogger.error(msg);
            return null;
        }
    }

    private byte[] salvarImagem(int index, String jsonResponse) {
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

            // Criar o diretório de saída se não existir
            File diretorioSaida = Paths.get("examples", "generated_images").toFile();
            if (!diretorioSaida.exists() && !diretorioSaida.mkdirs()) {
                throw new IllegalStateException("Não foi possível criar o diretório: " + diretorioSaida.getAbsolutePath());
            }

            // Gerar o caminho completo do arquivo
            String nomeArquivo = LocalDateTime.now() + "_generated_image_" + index + "_" + UUID.randomUUID() + ".png";
            File arquivoImagem = new File(diretorioSaida, nomeArquivo.replaceAll("[:]", "_")); // Substitui caracteres inválidos

            // Salvar a imagem no disco
            BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(imagemBytes));
            ImageIO.write(imagem, "png", arquivoImagem);

            log.info("Imagem salva com sucesso em: {}", arquivoImagem.getAbsolutePath());
            
            return imagemBytes;
        } catch (Exception e) {
            log.error("Erro ao carregar ou salvar a imagem", e);
            throw new RuntimeException("Falha ao processar a imagem: " + e.getMessage(), e);
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
