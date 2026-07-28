package com.labourconnect.entity;

import com.labourconnect.enums.ConversationRole;
import com.labourconnect.enums.ConversationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_states")
@Getter
@Setter
@NoArgsConstructor
public class ConversationState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    // Name of the current step in whatever flow this phone number is in
    // (e.g. "AWAITING_AREA") - defined and interpreted by the future
    // MessageRouterService, not by this entity.
    private String currentStep;

    // Accumulated answers collected so far in the current conversation, stored as
    // a JSON string (e.g. {"serviceType":"PLUMBER","area":"South Ex"}). Mapped to
    // MySQL's native JSON type for write-time validation and future querying.
    // Parsing/serialization is handled by the future flow-handling code, not here.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String contextData;

    // Which marketplace role (CLIENT / WORKER) this conversation is currently
    // operating as. Decided by RoleResolutionService, persisted here so the
    // same role is used consistently across every message in the session.
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(30)")
    private ConversationRole activeRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private ConversationStatus status = ConversationStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}