package br.com.blackhunter.bots.yui.entity;

import br.com.blackhunter.bots.yui.constants.Plataform;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformIntegration {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User userId;

    @Enumerated(EnumType.STRING)
    private Plataform plataform;
    private String apiKey;
    private String apiSecret;
    private LocalDateTime createdAt;
}

