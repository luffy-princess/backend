package com.phishme.backend.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.phishme.backend.commons.StringEncryptor;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Users {
    @Id
    @GenericGenerator(name = "global_seq_id", strategy = "com.phishme.backend.commons.SnowFlakeGenerator")
    @GeneratedValue(generator = "global_seq_id")
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Convert(converter = StringEncryptor.class)
    @Column(name = "provider", nullable = false, length = 60)
    private String provider;

    @Convert(converter = StringEncryptor.class)
    @Column(columnDefinition = "text", nullable = false, unique = true)
    private String email;

    @Convert(converter = StringEncryptor.class)
    @Column(name = "nickname", nullable = false, length = 60)
    private String nickname;

    @Convert(converter = StringEncryptor.class)
    @Column(nullable = false, length = 50)
    private String gender;

    @Convert(converter = StringEncryptor.class)
    @Column(name = "birth_date", nullable = false, length = 60)
    private String birthDate;

    @Convert(converter = StringEncryptor.class)
    @Column(nullable = false, length = 80)
    private String role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<TermAgreement> termAgreements;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<TrainingProgress> trainProgresses;
}
