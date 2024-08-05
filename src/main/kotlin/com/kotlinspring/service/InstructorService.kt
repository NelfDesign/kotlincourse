package com.kotlinspring.service

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.dto.InstructorDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.entity.Instructor
import com.kotlinspring.repository.InstructorRepository
import com.kotlinspring.service.CourseService.Companion.logger
import org.springframework.stereotype.Service
import java.util.*

@Service
class InstructorService( val instructorRepository: InstructorRepository) {

    fun createInstructor( instructorDTO: InstructorDTO): InstructorDTO{
        val instructorEntity = instructorDTO.let {
            Instructor(it.id, it.name)
        }
        instructorRepository.save(instructorEntity)

        logger.info("Saved instructor = $instructorEntity")

        return instructorEntity.let {
            InstructorDTO(it.id, it.name)
        }
    }

    fun findByInstructorId(instructorId: Int): Optional<Instructor> {
        return instructorRepository.findById(instructorId)
    }

}
