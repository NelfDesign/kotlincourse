package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.service.CourseService
import com.kotlinspring.util.courseDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals

@WebMvcTest(controllers = [CourseController::class])
@AutoConfigureWebTestClient
class CourseControllerUnitTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var courseServiceMock: CourseService

    @Test
    fun addCourse() {
        val courseDto = CourseDTO(null, "Build RestFull Api with Kotlin and SpringBoot", "Dilip",1)

        every { courseServiceMock.addCourse(courseDto) } returns courseDTO(id = 1)

        val result = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDto)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue{
            result!!.id != null
        }
    }

    @Test
    fun addCourse_validation() {
        val courseDto = CourseDTO(null, "", "",1)

        every { courseServiceMock.addCourse(courseDto) } returns courseDTO(id = 1)

        val result = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDto)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals("courseDto.category must not be blank, courseDto.name must not be blank", result)
    }

    @Test
    fun addCourse_runTimeException() {
        val courseDto = CourseDTO(null, "Build RestFull Api with Kotlin and SpringBoot", "Dilip",1)

        val errorMessage = "Unexpected Exception"

        every { courseServiceMock.addCourse(courseDto) } throws RuntimeException(errorMessage)

        val result = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDto)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals(errorMessage, result)
    }

    @Test
    fun retrieveAllCourses(){
        every { courseServiceMock.retrieveAllCourses(any()) }.returnsMany(
           listOf(
               courseDTO(id = 1),
               courseDTO(id = 2, name = "Build Reactive Microservices using Spring WebFlux/SpringBoot")
           )
        )

        val coursesDTOs = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        println("Courses are $coursesDTOs")
        Assertions.assertEquals(2, coursesDTOs!!.size)
    }

    @Test
    fun updateCourse(){
        // existing course
        var course = Course(null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development")
       every { courseServiceMock.updateCourse(any(), any()) } returns courseDTO(
           id = 100,
           name ="Build RestFul APis using SpringBoot and Kotlin1" )
        // courseId
        // update course
        val updateCourseDTO =  CourseDTO(null,
            "Build RestFul APis using SpringBoot and Kotlin1", "Development")

        val updatedCourse = webTestClient
            .put()
            .uri("/v1/courses/{course_id}", 100)
            .bodyValue(updateCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals("Build RestFul APis using SpringBoot and Kotlin1", updatedCourse!!.name)
    }

    @Test
    fun deleteCourse(){
     every { courseServiceMock.deleteCourse(any()) } just runs

        val updatedCourse = webTestClient
            .delete()
            .uri("/v1/courses/{course_id}", 100)
            .exchange()
            .expectStatus().isNoContent
    }
}