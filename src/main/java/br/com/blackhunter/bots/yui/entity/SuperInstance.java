package br.com.blackhunter.bots.yui.entity;

import br.com.blackhunter.bots.yui.constants.InstanceStatus;
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
public class SuperInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID instanceId;

    @ManyToOne
    private User userId;
    private String name;

    @Enumerated(EnumType.STRING)
    private InstanceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean active;
    private String analysisThreadId;

    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Opportunity> opportunities;
    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<GeneratedImage> images;
}
