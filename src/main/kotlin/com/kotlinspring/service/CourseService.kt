package com.kotlinspring.service

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.ecxeption.CourseNotFoundException
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class CourseService(val courseRepository: CourseRepository) {

    companion object : KLogging()

    fun addCourse( courseDTO: CourseDTO): CourseDTO{
        val courseEntity = courseDTO.let {
            Course(null, it.name, it.category)
        }
        courseRepository.save(courseEntity)

        logger.info("Saved course = $courseEntity")

        return courseEntity.let {
            CourseDTO(it.id, it.name, it.category)
        }
    }

    fun retrieveAllCourses() : List<CourseDTO>{
       return courseRepository.findAll()
           .map {
               CourseDTO(it.id, it.name, it.category)
           }
    }

    fun updateCourse(courseDTO: CourseDTO, courseId: Int): CourseDTO {
        val existingCourse = courseRepository.findById(courseId)

       return if (existingCourse.isPresent){
            existingCourse.get()
                .let {
                    it.name = courseDTO.name
                    it.category = courseDTO.category
                    courseRepository.save(it)
                    CourseDTO(it.id, it.name, it.category)
                }
        }else{
            throw CourseNotFoundException("No course found for the passing id : $courseId")
        }
    }

    fun deleteCourse(courseId: Int) {
        val existingCourse = courseRepository.findById(courseId)

        return if (existingCourse.isPresent){
            existingCourse.get()
                .let {
                    courseRepository.deleteById(courseId)

                }
        }else{
            throw CourseNotFoundException("No course found for the passing id : $courseId")
        }
    }
}
