package nl.avisi.tech.lab.inventory.messaging

import org.slf4j.MDC
import java.util.*

object CorrelationHelper {
    private const val CORRELATION_ID_MDC_KEY = "BeerCorrelationId"

    fun setOrGenerate(existingCorrelationId: String?): String {
        val correlationId = existingCorrelationId ?: UUID.randomUUID().toString()
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId)
        return correlationId
    }

    fun getCorrelationId(): String = MDC.get(CORRELATION_ID_MDC_KEY) ?: throw IllegalStateException("Correlation id requested outside of correlation scope")

    fun clear() {
        MDC.remove(CORRELATION_ID_MDC_KEY)
    }
}

fun withCorrelationId(correlationId: String? = null, block: () -> Unit) =
    try {
        CorrelationHelper.setOrGenerate(correlationId)
        block()
    } finally {
        CorrelationHelper.clear()
    }
