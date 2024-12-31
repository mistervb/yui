package br.com.blackhunter.bots.yui.util;

import br.com.blackhunter.bots.yui.dto.TrendingImageDTO;

public class PromptGenerator {

    public static String generatePrompt(TrendingImageDTO trendingImage) {
        StringBuilder prompt = new StringBuilder();

        // Add description
        if (trendingImage.getDescription() != null && !trendingImage.getDescription().isEmpty()) {
            prompt.append(trendingImage.getDescription()).append(" ");
        }

        // Add tags
        if (trendingImage.getTags() != null && !trendingImage.getTags().isEmpty()) {
            prompt.append("Tags: ").append(trendingImage.getTags()).append(". ");
        }

        // Add category
        if (trendingImage.getCategory() != null && !trendingImage.getCategory().isEmpty()) {
            prompt.append("Category: ").append(trendingImage.getCategory()).append(". ");
        }

        // Add image type
        if (trendingImage.getImageType() != null && !trendingImage.getImageType().isEmpty()) {
            prompt.append("Type of image: ").append(trendingImage.getImageType()).append(". ");
        }

        // Add media type
        if (trendingImage.getMediaType() != null && !trendingImage.getMediaType().isEmpty()) {
            prompt.append("Media type: ").append(trendingImage.getMediaType()).append(". ");
        }

        // Add resolution
        if (trendingImage.getResolution() != null && !trendingImage.getResolution().isEmpty()) {
            prompt.append("Resolution: ").append(trendingImage.getResolution()).append(". ");
        }

        // Add color palette
        if (trendingImage.getColorPalette() != null && !trendingImage.getColorPalette().isEmpty()) {
            prompt.append("Color palette: ").append(trendingImage.getColorPalette()).append(". ");
        }

        // Add style
        if (trendingImage.getStyle() != null && !trendingImage.getStyle().isEmpty()) {
            prompt.append("Art style: ").append(trendingImage.getStyle()).append(". ");
        }

        // Add intended use
        if (trendingImage.getIntendedUse() != null && !trendingImage.getIntendedUse().isEmpty()) {
            prompt.append("Intended use: ").append(trendingImage.getIntendedUse()).append(". ");
        }

        // Add detail level
        if (trendingImage.getDetailLevel() != null && !trendingImage.getDetailLevel().isEmpty()) {
            prompt.append("Detail level: ").append(trendingImage.getDetailLevel()).append(". ");
        }

        // Add priority
        prompt.append("This image is trending with a priority score of ").append(trendingImage.getPriority()).append(". ");

        // Add source
        if (trendingImage.getSource() != null && !trendingImage.getSource().isEmpty()) {
            prompt.append("Data source: ").append(trendingImage.getSource()).append(". ");
        }

        return prompt.toString().trim();
    }

    public static void main(String[] args) {
        // Example usage
        TrendingImageDTO exampleTrending = new TrendingImageDTO();
        exampleTrending.setDescription("Group of multigenerational people hugging each other - Support, multiracial and diversity concept - Main focus on senior man with white hairs");
        exampleTrending.setTags("support, multiracial, diversity concept");
        exampleTrending.setCategory("Abstractos");
        exampleTrending.setImageType("photo");
        exampleTrending.setMediaType("image");
        exampleTrending.setResolution("100x150");
        exampleTrending.setColorPalette("gold, blue, white");
        exampleTrending.setStyle("general");
        exampleTrending.setIntendedUse("general use");
        exampleTrending.setDetailLevel("high-detail");
        exampleTrending.setPriority(5);
        exampleTrending.setSource("Shutterstock");

        String prompt = generatePrompt(exampleTrending);
        System.out.println("Generated Prompt:\n" + prompt);
    }
}
