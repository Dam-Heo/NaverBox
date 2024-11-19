package com.zerobase.naverbox.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_tb")
@DynamicInsert
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "password")
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'USER'")
    private UserRole userRole;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "sns_type")
    private String snsType;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
    
    @Builder
    public User(String userId, String password, String email, String name, UserRole userRole, String refreshToken, String snsType) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.userRole = userRole;
        this.refreshToken = refreshToken;
        this.snsType = snsType;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @Builder(builderMethodName = "updateRefreshTokenBuilder")
    public User(Long id, String refreshToken){
        this.id = id;
        this.refreshToken = refreshToken;
        this.updateAt = LocalDateTime.now();
    }
}
