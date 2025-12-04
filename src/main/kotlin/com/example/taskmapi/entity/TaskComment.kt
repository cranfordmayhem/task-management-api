package com.example.taskmapi.entity

import jakarta.persistence.*

@Entity
@Table(name = "task_comment")
data class TaskComment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    var task: Task,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: UserAccount,

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    var comment: String
) : Auditor()