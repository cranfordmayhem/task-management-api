package com.example.taskmapi.entity

import com.example.taskmapi.entity.enums.Role
import jakarta.persistence.*

@Entity
@Table(name="user_account")
data class UserAccount(
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable=false, unique=true, length=255)
    val email: String,

    @Column(nullable=false, length=255)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    val role: Role,

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = [ CascadeType.ALL ])
    var profile: UserProfile? = null,

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL] , orphanRemoval = true)
    val boards: MutableList<Board> = mutableListOf()

) : Auditor() {
    override fun toString() = "UserAccount(id=$id, email=$email)"
    override fun equals(other: Any?) = other is UserAccount && other.id == id
    override fun hashCode() = id.hashCode()
}