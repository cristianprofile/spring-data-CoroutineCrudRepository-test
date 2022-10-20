package com.example.cromero.demo

import kotlinx.coroutines.flow.Flow
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@SpringBootApplication
class DemoApplication



data class  PersonDto(val age: Int, val name : String)

@RestController
@RequestMapping("/persons")
class PersonController (val personService: PersonService)
{
    
    @GetMapping
    suspend fun getPersons() = personService.getAll()

    @PostMapping
    suspend fun savePerson(@RequestBody personDto: PersonDto) = personService.save(personDto)
    
}

interface PersonService {
    suspend fun getAll(): Flow<Person>
    suspend fun getById(id: String): Person?
    suspend fun save(personDto: PersonDto): Person
}

@Service
class PersonServiceImpl (private val personRepository: PersonRepository ) : PersonService
{
    
    override suspend fun getAll(): Flow<Person> {
        return personRepository.findAll()
    }

    override suspend fun getById(id: String): Person? {
        return personRepository.findById(id)
    }

    override suspend fun save(personDto: PersonDto): Person
    {
        return personRepository.save(personDto.convertToPerson())
    }

}

fun PersonDto.convertToPerson() =
    Person(
        name = name,
        age = age
    )


@Document
data class Person (@Id val id: String? =  null, val age: Int, val name : String)

@Repository
interface PersonRepository: CoroutineCrudRepository<Person, String>



fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
