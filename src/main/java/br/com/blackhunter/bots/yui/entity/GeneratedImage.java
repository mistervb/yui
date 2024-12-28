package br.com.blackhunter.bots.yui.entity;

import br.com.blackhunter.bots.yui.constants.GeneratedImageStatus;
import br.com.blackhunter.bots.yui.constants.Plataform;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneratedImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private SuperInstance instance;

    @ManyToOne
    private Opportunity opportunity;

    @Enumerated(EnumType.STRING)
    private Plataform plataform;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private GeneratedImageStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Sale sale;
}
