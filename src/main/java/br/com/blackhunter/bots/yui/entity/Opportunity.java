package br.com.blackhunter.bots.yui.entity;

import br.com.blackhunter.bots.yui.constants.OpportunityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Opportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private SuperInstance instance;

    private String details;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OpportunityStatus status;

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<GeneratedImage> images;
}
