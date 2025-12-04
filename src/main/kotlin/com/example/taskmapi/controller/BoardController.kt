package com.example.taskmapi.controller

import com.example.taskmapi.dto.*
import com.example.taskmapi.service.BoardService
import com.example.taskmapi.utils.AuthEmailUtil
import org.springframework.data.domain.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/board")
class BoardController(
    private val boardService: BoardService,
    private val authEmail: AuthEmailUtil
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@RequestBody boardReq: BoardRequest): BoardResponse {
        val email = authEmail.getUser()
        return boardService.create(boardReq, email)
    }

    @GetMapping("/my")
    fun getByMember(pageable: Pageable): Page<BoardResponse> {
        val email = authEmail.getUser()
        return boardService.retrieveByMember(email, pageable)
    }

    @GetMapping
    fun getAll(pageable: Pageable): Page<BoardResponse>{
        return boardService.retrieve(pageable)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): BoardResponse? {
        val email = authEmail.getUser()
        return boardService.retrieveById(id, email)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody boardReq: BoardUpdateRequest): BoardResponse {
        val email = authEmail.getUser()
        return boardService.updateBoard(id, boardReq, email)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): Unit? {
        val email = authEmail.getUser()
        return boardService.delete(id, email)
    }
}