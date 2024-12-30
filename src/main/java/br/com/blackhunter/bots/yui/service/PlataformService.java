package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.dto.TrendingImageDTO;
import br.com.blackhunter.bots.yui.entity.PlatformIntegration;

import java.util.List;

public interface PlataformService {
    List<TrendingImageDTO> getTrendingTags(PlatformIntegration plataform);
}
