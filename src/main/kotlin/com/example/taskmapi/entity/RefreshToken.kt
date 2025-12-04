package com.example.taskmapi.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name="refresh_tokens")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable=false, unique=true)
    val token: String,

    @Column(nullable=false)
    val expiryDate: Instant,

    @Column(nullable=false)
    var used: Boolean = false,

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_account_id", nullable=false)
    val userAccount: UserAccount
) : Auditor()