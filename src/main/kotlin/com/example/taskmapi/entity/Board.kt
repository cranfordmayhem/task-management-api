package com.example.taskmapi.entity

import jakarta.persistence.*

@Entity
@Table(name="board")
data class Board(
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable=false)
    var name: String,

    @Column(nullable=false)
    var description: String? = null,

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="owner_id")
    var owner: UserAccount,

    @OneToMany(
        mappedBy = "board",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var members: MutableSet<BoardMember> = mutableSetOf(),

    @OneToMany(mappedBy="board", cascade=[CascadeType.ALL], orphanRemoval = true)
    var tasks: MutableList<Task> = mutableListOf()
) : Auditor(){
    override fun toString() = "Board(id=$id, name=$name)"
    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = (other is Board && other.id == id)
}