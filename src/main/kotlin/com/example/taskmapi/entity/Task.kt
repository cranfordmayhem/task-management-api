package com.example.taskmapi.entity

import com.example.taskmapi.entity.enums.TaskStatus
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "task")
data class Task(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: TaskStatus = TaskStatus.TODO,

    @Column(name="position", nullable=false)
    var position: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    var board: Board,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    var assignee: UserAccount? = null,

    @Column(name = "due_date")
    var dueDate: LocalDate? = null,

    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableList<TaskComment> = mutableListOf()

) : Auditor() {
    override fun toString() = "Task(id=$id, title=$title)"
    override fun hashCode() = id.hashCode()
    override fun equals(other: Any?) = (other is Task && other.id == id)
}