package br.com.blackhunter.bots.yui.config;

import br.com.blackhunter.bots.yui.constants.Plataform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.blackhunter.bots.yui.constants.Util.NO_API_KEY;
import static br.com.blackhunter.bots.yui.constants.Util.NO_API_SECRET;
import static br.com.blackhunter.bots.yui.constants.Util.YUI_VERSION;

@Component
public class PlataformControl {
    @Value("${shutterstock.api.key}")
    private String shutterstockApiKey;
    @Value("${shutterstock.api.secret}")
    private String shutterstockApiSecret;

    @Value("${adobe.stock.api.key}")
    private String adobeStockApiKey;
    @Value("${adobe.stock.api.secret}")
    private String adobeStockApiSecret;

    @Value("${etsy.api.key}")
    private String etsyApiKey;
    @Value("${etsy.api.secret}")
    private String etsyApiSecret;

    public List<Plataform> getAllPlataforms() {
        return new ArrayList<>(List.of(Plataform.values()));
    }

    public ApiData getApiData(Plataform plataform) {
        switch (plataform) {
            case Shutterstock:
                return this.new ApiData(shutterstockApiKey, shutterstockApiSecret);
            case Adobe_Stock:
                return this.new ApiData(adobeStockApiKey, adobeStockApiSecret);
            case Etsy:
                return this.new ApiData(etsyApiKey, etsyApiSecret);
            case Creative_Market:
                return this.new ApiData(NO_API_KEY, NO_API_SECRET);
            default:
                throw new IllegalArgumentException("Plataforma não suportada nesta versão: " + YUI_VERSION);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ApiData {
        private String key;
        private String secret;
    }
}

