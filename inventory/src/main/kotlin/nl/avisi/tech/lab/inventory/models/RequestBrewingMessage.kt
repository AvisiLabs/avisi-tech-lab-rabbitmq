package nl.avisi.tech.lab.inventory.models

data class RequestBrewingMessage(
    val beer: Beer,
    val hopAmount: Int,
    val maltAmount: Int
)
