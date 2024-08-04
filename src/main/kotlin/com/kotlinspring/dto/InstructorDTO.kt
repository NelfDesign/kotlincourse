package com.kotlinspring.dto

import jakarta.validation.constraints.NotBlank

data class InstructorDTO (
    val id :Int?,
    @get:NotBlank(message = "instructorDto.name must not be blank")
    val name : String
)