package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.entity.PlatformIntegration;

import java.util.List;

public interface PlataformService {
    List<String> getTrendingTags(PlatformIntegration plataform);
}
