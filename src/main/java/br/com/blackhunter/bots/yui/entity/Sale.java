package br.com.blackhunter.bots.yui.entity;

import br.com.blackhunter.bots.yui.constants.Plataform;
import br.com.blackhunter.bots.yui.constants.SaleStatus;
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
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private GeneratedImage image;
    @Enumerated(EnumType.STRING)
    private Plataform plataform;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
