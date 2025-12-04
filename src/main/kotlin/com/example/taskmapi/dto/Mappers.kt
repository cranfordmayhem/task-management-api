package com.example.taskmapi.dto

import com.example.taskmapi.entity.*
import com.example.taskmapi.entity.enums.Role
import java.time.Instant

fun UserAccountRequest.toEntity(role: Role):UserAccount = UserAccount(
    email = this.email,
    password = this.password,
    role = role
)

fun AccountUpdateRequest.toEntity(): UserAccount = UserAccount(
    email = this.email,
    password = this.password,
    role = this.role
)

fun UserAccount.toResponse(): UserAccountResponse = UserAccountResponse(
    id = this.id,
    email = this.email,
    role = this.role
)

fun RefreshTokenRequest.toEntity(userAccount: UserAccount, expiryDate: Instant):
        RefreshToken = RefreshToken(
            token = this.refreshToken,
            expiryDate = expiryDate,
            userAccount = userAccount
        )

fun UserProfileRequest.toEntity(userAccount: UserAccount): UserProfile = UserProfile(
    firstName = this.firstName,
    lastName = this.lastName,
    age = this.age,
    user = userAccount
)

fun UserProfile.toResponse(): UserProfileResponse = UserProfileResponse(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    age = this.age,
    email = this.user.email
)

fun BoardRequest.toEntity(user: UserAccount): Board = Board(
    name = this.name,
    description = this.description,
    owner = user
)

fun Board.toResponse(): BoardResponse = BoardResponse(
    id = this.id,
    name = this.name,
    description = this.description,
    owner = "${owner.profile?.firstName} ${owner.profile?.lastName}",
    members = members.map { it.toResponse() },
    tasks = tasks.map { it.toResponse() }
)

fun BoardMemberRequest.toEntity(board: Board, user: UserAccount): BoardMember = BoardMember(
    id = BoardMemberId(
        boardId = this.boardId,
        userId = this.userId
    ),
    board = board,
    user = user,
    role = this.role
)

fun BoardMember.toResponse(): BoardMemberResponse = BoardMemberResponse(
    userId = this.user.id,
    email = this.user.email,
    roles = this.role
)

fun TaskRequest.toEntity(board: Board): Task = Task(
    title = this.title,
    description = this.description,
    board = board
)

fun Task.toResponse(): TaskResponse = TaskResponse(
    id = this.id!!,
    title = this.title,
    description = this.description,
    status = this.status,
    position = this.position,
    assignee = assignee?.email,
    comments = comments.map { it.toResponse() },
    boardId = this.board.id
)

fun TaskCommentRequest.toEntity(task: Task, author: UserAccount): TaskComment = TaskComment(
    task = task,
    comment = this.content,
    author = author
)

fun TaskComment.toResponse(): TaskCommentResponse = TaskCommentResponse(
    id = this.id!!,
    content = this.comment,
    authorId = this.author.id,
    authorEmail = this.author.email,
    createdAt = this.createdAt!!
)

fun Task.toShallowResponse(): TaskResponse = TaskResponse(
    id = this.id!!,
    title = this.title,
    description = this.description,
    status = this.status,
    position = this.position,
    assignee = this.assignee?.email,
    comments = emptyList(),
    boardId = this.board.id
)