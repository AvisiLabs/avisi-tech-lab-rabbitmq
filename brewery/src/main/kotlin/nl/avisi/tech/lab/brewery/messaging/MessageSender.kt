package nl.avisi.tech.lab.brewery.messaging

import mu.KLogging
import nl.avisi.tech.lab.brewery.messaging.MessagingConfiguration.Companion.EXCHANGE_NAME
import nl.avisi.tech.lab.brewery.models.ServeBrewingMessage
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate

class MessageSender(
    private val rabbitTemplate: RabbitTemplate,
    private val applicationName: String
) {
    companion object: KLogging()

    fun sendMessage(message: ServeBrewingMessage, routingKey: String) {
        logger.info("Sending message to [$routingKey] from $applicationName")
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, message, this::addCorrelationIdToMessage)
    }

    private fun addCorrelationIdToMessage(message: Message): Message =
        message.apply {
            messageProperties.setHeader(MessagingConfiguration.CORRELATION_HEADER_KEY, CorrelationHelper.getCorrelationId())
        }
}
