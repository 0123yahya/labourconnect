package com.labourconnect.entity;

import com.labourconnect.enums.JobStatus;
import com.labourconnect.enums.Skill;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private Skill serviceType;

    @Column(nullable = false)
    private String area;

    private LocalDate preferredDate;

    private String budget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private JobStatus status = JobStatus.REQUESTED;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}