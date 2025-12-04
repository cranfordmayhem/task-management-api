package com.example.taskmapi.controller

import com.example.taskmapi.dto.*
import com.example.taskmapi.service.BoardMemberService
import com.example.taskmapi.utils.AuthEmailUtil
import org.springframework.data.domain.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/board-member")
class BoardMemberController(
    private val memberService: BoardMemberService,
    private val authEmail: AuthEmailUtil
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add/board/{boardId}/user/{userId}")
    fun add(@PathVariable boardId: Long, @PathVariable userId: Long): BoardMemberResponse {
        val email = authEmail.getUser()
        return memberService.addMember(boardId, userId, email)
    }

    @GetMapping("/board/{boardId}")
    fun retrieve(@PathVariable boardId: Long, pageable: Pageable): Page<BoardMemberResponse> =
        memberService.retrieveByBoardId(boardId, pageable)

    @GetMapping("/board/{boardId}/user/{userId}")
    fun retrieveById(@PathVariable boardId: Long, @PathVariable userId: Long): BoardMemberResponse =
        memberService.retrieveById(boardId, userId)

    @DeleteMapping("/board/{boardId}/user/{userId}")
    fun delete(@PathVariable boardId: Long, @PathVariable userId: Long): Unit? {
        val email = authEmail.getUser()
        return memberService.removeMember(boardId, userId, email)
    }
}