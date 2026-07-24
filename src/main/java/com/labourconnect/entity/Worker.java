package com.labourconnect.entity;

import com.labourconnect.enums.Skill;
import com.labourconnect.enums.WorkerStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "workers")
@Getter
@Setter
@NoArgsConstructor
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private Skill skill;

    @Column(nullable = false)
    private String area;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private WorkerStatus status = WorkerStatus.ACTIVE;

    @Column(nullable = false)
    private int noShowCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}