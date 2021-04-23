package nl.avisi.tech.lab.brewery.models

data class RequestBrewingMessage(
    val beer: Beer,
    val hopAmount: Int,
    val maltAmount: Int
)
