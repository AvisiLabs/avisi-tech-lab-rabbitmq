package nl.avisi.tech.lab.brewery.services

import mu.KLogging
import nl.avisi.tech.lab.brewery.models.Beer
import nl.avisi.tech.lab.brewery.models.ServeBrewingMessage
import org.springframework.stereotype.Service
import java.util.*
import kotlin.system.measureTimeMillis

@Service
class BrewingService {
    companion object : KLogging() {
        private const val MIN_SLEEP_TIME = 2
        private const val MAX_SLEEP_TIME = 10
        private const val SECONDS_TO_MILLIS_FACTOR = 1000
    }


    fun processBrewingRequest(beer: Beer, hopAmount: Int, maltAmount: Int): ServeBrewingMessage {
        logger.info("Brewing $beer using the following ingredients. Hop: $hopAmount and Malt: $maltAmount")

        val duration = measureTimeMillis {
            logger.info("Malting...")
            sleepForRandomTime()
            logger.info("Milling...")
            sleepForRandomTime()
            logger.info("Mashing...")
            sleepForRandomTime()
            logger.info("Lautering...")
            sleepForRandomTime()
            logger.info("Boiling...")
            sleepForRandomTime()
            logger.info("Fermenting...")
            sleepForRandomTime()
            logger.info("Conditioning...")
            sleepForRandomTime()
            logger.info("Filtering...")
            sleepForRandomTime()
            logger.info("Packaging...")
            sleepForRandomTime()
        }

        return ServeBrewingMessage(
            beer = beer,
            brewingTime = (duration / SECONDS_TO_MILLIS_FACTOR).toInt()
        )
    }


    private fun sleepForRandomTime() {
        val sleepTime = MIN_SLEEP_TIME + Random().nextInt(MAX_SLEEP_TIME)
        Thread.sleep((sleepTime * SECONDS_TO_MILLIS_FACTOR).toLong())
    }
}
