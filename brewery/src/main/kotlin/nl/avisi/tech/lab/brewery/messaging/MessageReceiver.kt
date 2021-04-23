package nl.avisi.tech.lab.brewery.messaging

import com.rabbitmq.client.Channel
import mu.KLogging
import nl.avisi.tech.lab.brewery.models.RequestBrewingMessage
import nl.avisi.tech.lab.brewery.services.BrewingService
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class BrewingRequestMessageReceiver(
    private val brewingService: BrewingService,
    private val messageSender: MessageSender
) {
    companion object : KLogging() {
        private const val BEER_SERVING_ROUTING_KEY = "beer.served"
    }

    @RabbitListener(queues = [MessagingConfiguration.QUEUE_NAME])
    fun receiveMessage(receivedMessage: RequestBrewingMessage, message: Message, channel: Channel) =
        processMessage(receivedMessage, message, channel) {
            val serveBrewingMessage = brewingService.processBrewingRequest(receivedMessage.beer, receivedMessage.hopAmount, receivedMessage.maltAmount)
            messageSender.sendMessage(serveBrewingMessage, BEER_SERVING_ROUTING_KEY)
        }

    protected fun processMessage(receivedMessage: RequestBrewingMessage, message: Message, channel: Channel, handleFunction: (RequestBrewingMessage) -> Unit) =
        withCorrelationId(message.messageProperties.headers[MessagingConfiguration.CORRELATION_HEADER_KEY]?.toString()) {
            logger.info("Received ${receivedMessage::class.simpleName} in ${this::class.simpleName}")
            handleFunction(receivedMessage)
            channel.basicAck(message.messageProperties.deliveryTag, false)
        }
}
