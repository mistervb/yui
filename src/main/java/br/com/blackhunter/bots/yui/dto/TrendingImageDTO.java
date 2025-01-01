package br.com.blackhunter.bots.yui.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendingImageDTO {
    private String description;  // Descrição detalhada da imagem
    private String tags;         // Tags associadas à descrição
    private String previewUrl;   // URL da preview da imagem
    private String imageType;    // Tipo de imagem (photo, illustration, etc.)
    private String mediaType;    // Tipo de mídia (image, gif, video, etc.)
    private String category;     // Categoria associada à imagem (art, nature, etc.)
    private String resolution;    // Resolução da imagem
    private String colorPalette;  // Paleta de cores sugerida
    private String intendedUse;   // Contexto de uso
    private String detailLevel;   // Nível de detalhamento
    private String style;         // Estilo artístico
    private int priority;         // Popularidade/prioridade
    private String source;        // Fonte dos dados
}
