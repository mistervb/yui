package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.dto.TrendingImageDTO;
import br.com.blackhunter.bots.yui.entity.PlatformIntegration;
import br.com.blackhunter.bots.yui.log.YuiLogger;
import br.com.blackhunter.bots.yui.util.HttpClientUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ShutterstockService implements PlataformService {
    @Autowired
    private HttpClientUtil httpClientUtil;

    @Override
    public List<TrendingImageDTO> getTrendingTags(PlatformIntegration platform) {
        String url = "https://api.shutterstock.com/v2/images/search";
        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + platform.getApiKey(),
                "Accept", "application/json"
        );

        try {
            // Obtenção das categorias
            ResponseEntity<CategoryResponse> categoryResponse = httpClientUtil.get(
                    "https://api.shutterstock.com/v2/images/categories?language=es", headers, CategoryResponse.class
            );

            if (categoryResponse.getBody() == null || categoryResponse.getBody().getData() == null) {
                YuiLogger.warn("[ShutterstockService] - Não foi possível obter categorias.");
                return List.of();
            }

            List<String> categories = categoryResponse.getBody().getData().stream()
                    .map(Category::getName)
                    .collect(Collectors.toList());

            List<TrendingImageDTO> trendingImages = new ArrayList<>();

            // Iterando sobre as categorias e buscando imagens para cada uma
            for (String category : categories) {
                String searchUrl = url + "?category=" + category + "&sort=popular&per_page=10";
                ResponseEntity<ShutterstockResponse> response = httpClientUtil.get(searchUrl, headers, ShutterstockResponse.class);

                if (response.getBody() == null || response.getBody().getData() == null) {
                    YuiLogger.warn("[ShutterstockService] - A resposta da API para a categoria " + category + " veio vazia ou nula.");
                    continue;
                }

                // Processamento para criar TrendingImageDTO
                List<TrendingImageDTO> categoryImages = response.getBody().getData().stream()
                        .filter(image -> image.getDescription() != null) // Filtra imagens sem descrição
                        .filter(distinctByKey(ImageData::getDescription)) // Remove duplicatas com base na descrição
                        .map(image -> {
                            String description = image.getDescription();
                            String previewUrl = image.getAssets().getPreview().getUrl();
                            String imageType = image.getImageType();
                            String mediaType = image.getMediaType();
                            String resolution = image.getAssets().getLargeThumb().getHeight() + "x" + image.getAssets().getLargeThumb().getWidth();
                            String intendedUse = Map.of(
                                    "Nature", "background",
                                    "Art", "website banner",
                                    "People", "social media post"
                            ).getOrDefault(category, "general use");
                            String detailLevel = description.split(" ").length > 10 ? "high-detail" : "minimalistic";
                            int priority = 5; // Padrão ou baseado em popularidade futura
                            String source = "Shutterstock";

                            List<String> tags = Arrays.stream(description.split(",| - |\\.")) // Filtra tags
                                    .map(String::trim)
                                    .map(String::toLowerCase)
                                    .filter(tag -> !tag.isBlank() && tag.length() > 3)
                                    .filter(ShutterstockService::isRelevant)
                                    .filter(tag -> hasValidLength(tag, 4))
                                    .distinct()
                                    .collect(Collectors.toList());

                            String style = tags.contains("watercolor") ? "watercolor" :
                                    tags.contains("line art") ? "line art" : "general";

                            return new TrendingImageDTO(
                                    description,
                                    String.join(", ", tags),
                                    previewUrl,
                                    imageType,
                                    mediaType,
                                    category,
                                    resolution,
                                    "gold, blue, white",  // Paleta estática por enquanto
                                    intendedUse,
                                    detailLevel,
                                    style,
                                    priority,
                                    source
                            );
                        })
                        .filter(dto -> dto.getTags() != null && !dto.getTags().isEmpty())
                        .collect(Collectors.toList());

                trendingImages.addAll(categoryImages);
            }

            return trendingImages;

        } catch (Exception e) {
            String msg = "[ShutterstockService] - ERROR: Erro ao obter tendências do Shutterstock: " + e.getMessage();
            System.err.println(msg);
            YuiLogger.error(msg);
            return List.of();
        }
    }

    /**
     * Valida se a tag é relevante, removendo palavras irrelevantes.
     */
    private static boolean isRelevant(String tag) {
        List<String> irrelevantWords = Arrays.asList("Main focus on", "face", "indoors");
        return irrelevantWords.stream().noneMatch(tag::contains);
    }

    /**
     * Valida se a tag tem um número aceitável de palavras.
     */
    private static boolean hasValidLength(String tag, int maxWords) {
        return tag.split("\\s+").length <= maxWords;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


    // Classes auxiliares para resposta da API
    public static class ShutterstockResponse {
        private List<ImageData> data;

        public List<ImageData> getData() {
            return data;
        }

        public void setData(List<ImageData> data) {
            this.data = data;
        }
    }

    @Data
    public static class ImageData {
        private String id; // ID da imagem na API
        private String description; // Descrição da imagem
        @JsonProperty("image_type")
        private String imageType; // Tipo de imagem
        @JsonProperty("media_type")
        private String mediaType; // Tipo da mídia
        private Asset assets; // Contém as URLs dos recursos, como preview e thumbnail

        @Override
        public String toString() {
            return "ImageData{" +
                    "id='" + id + '\'' +
                    ", description='" + description + '\'' +
                    ", assets=" + assets +
                    '}';
        }

        // Classe interna para mapear os assets da imagem
        @Data
        public static class Asset {
            private Preview preview; // Detalhes do preview
            @JsonProperty("small_thumb")
            private Thumbnail smallThumb; // Miniatura pequena
            @JsonProperty("large_thumb")
            private Thumbnail largeThumb; // Miniatura grande
            private Thumbnail mosaic; // Miniatura em mosaico
            @JsonProperty("huge_thumb")
            private Thumbnail hugeThumb; // Miniatura enorme
            @JsonProperty("preview_1000")
            private Preview preview1000; // Preview com largura de 1000px
            @JsonProperty("preview_1500")
            private Preview preview1500; // Preview com largura de 1500px

            @Override
            public String toString() {
                return "Asset{" +
                        "preview=" + preview +
                        ", smallThumb=" + smallThumb +
                        ", largeThumb=" + largeThumb +
                        ", mosaic=" + mosaic +
                        ", hugeThumb=" + hugeThumb +
                        ", preview1000=" + preview1000 +
                        ", preview1500=" + preview1500 +
                        '}';
            }
        }

        @Data
        public static class Thumbnail {
            private String url; // URL da imagem
            private int width; // Largura da imagem
            private int height; // Altura da imagem

            @Override
            public String toString() {
                return "Thumbnail{" +
                        "url='" + url + '\'' +
                        ", width=" + width +
                        ", height=" + height +
                        '}';
            }
        }

        // Classe interna para mapear os detalhes do preview
        @Data
        public static class Preview {
            private String url; // URL do preview da imagem
            private int width; // Largura do preview
            private int height; // Altura do preview

            @Override
            public String toString() {
                return "Preview{" +
                        "url='" + url + '\'' +
                        ", width=" + width +
                        ", height=" + height +
                        '}';
            }
        }
    }

    public static class CategoryResponse {
        private List<Category> data;

        public List<Category> getData() {
            return data;
        }

        public void setData(List<Category> data) {
            this.data = data;
        }
    }

    @Data
    public static class Category {
        private String id;   // ID da categoria
        private String name; // Nome da categoria

        @Override
        public String toString() {
            return "Category{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
