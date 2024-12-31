package br.com.blackhunter.bots.yui.constants;

public enum ImageAIModel {
    FLUX_1_SCHNELL("ai/run/@cf/black-forest-labs/flux-1-schnell", "flux-1-schnell");
    private String endpoint;
    private String modelName;

    ImageAIModel(String endpoint, String modelName) {
        this.endpoint = endpoint;
        this.modelName = modelName;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getModelName() {
        return this.modelName;
    }
}
