package nl.avisi.tech.lab.brewery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BreweryApplication

fun main(args: Array<String>) {
	runApplication<BreweryApplication>(*args)
}
