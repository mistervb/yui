package br.com.blackhunter.bots.yui.dto;

import br.com.blackhunter.bots.yui.constants.Plataform;
import br.com.blackhunter.bots.yui.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageProduct {
    private String description;
    byte[] imageBytes;
    double price;
    Plataform plataform;
    User user;
}
