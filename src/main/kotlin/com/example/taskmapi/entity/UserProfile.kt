package com.example.taskmapi.entity

import jakarta.persistence.*

@Entity
@Table(name="user_profile")
data class UserProfile(
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, unique=true)
    val user: UserAccount,

    @Column(name="first_name", length=255, nullable=false)
    val firstName: String,

    @Column(name="last_name", length=255, nullable=false)
    val lastName: String,

    @Column(nullable=false)
    val age: Int
) : Auditor()