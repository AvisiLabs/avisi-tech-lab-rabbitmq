package nl.avisi.tech.lab.brewery.models

data class ServeBrewingMessage(
    val beer: Beer,
    val brewingTime: Int
)
