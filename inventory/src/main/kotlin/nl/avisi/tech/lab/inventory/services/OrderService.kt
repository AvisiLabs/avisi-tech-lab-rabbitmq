package nl.avisi.tech.lab.inventory.services

import mu.KLogging
import nl.avisi.tech.lab.inventory.models.Beer
import nl.avisi.tech.lab.inventory.models.RequestBrewingMessage
import org.springframework.stereotype.Service

@Service
class OrderService(private val supplierService: SupplierService) {

    companion object : KLogging()

    fun processOrderRequest(beer: Beer): RequestBrewingMessage {
        val (requiredHopAmount, requiredMaltAmount) = determineRequiredAmounts(beer)

        require(supplierService.requestHop(requiredHopAmount) && supplierService.requestMalt(requiredMaltAmount)) {
            "Supplier could not supply required ingredients."
        }

        logger.info("Determined that $requiredHopAmount hop and $requiredMaltAmount malt is needed to brew $beer")

        return RequestBrewingMessage(
            beer = beer,
            hopAmount = requiredHopAmount,
            maltAmount = requiredMaltAmount
        )
    }

    private fun determineRequiredAmounts(beer: Beer): Pair<Int, Int> =
        when (beer) {
            Beer.WILLY_TONKA -> 3 to 10
            Beer.ROCK_OUT_WITH_YOUR_BOCK_OUT -> 3 to 8
            Beer.SERGEANT_PEPPER -> 6 to 6
        }
}
