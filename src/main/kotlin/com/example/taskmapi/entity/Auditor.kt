package com.example.taskmapi.entity

import jakarta.persistence.*
import org.springframework.data.annotation.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Auditor {

    @Column(name="created_date", updatable=false)
    @CreatedDate
    var createdAt: Instant? = null

    @Column(name="created_by", updatable=false)
    @CreatedBy
    var createdBy: String? = null

    @Column(name="last_modified_date")
    @LastModifiedDate
    var updatedAt: Instant? = null

    @Column(name="last_modified_by")
    @LastModifiedBy
    var updatedBy: String? = null

}