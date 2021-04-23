package nl.avisi.tech.lab.inventory.messaging

import com.rabbitmq.client.Channel
import mu.KLogging
import nl.avisi.tech.lab.inventory.models.OrderRequestedMessage
import nl.avisi.tech.lab.inventory.services.OrderService
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class OrderRequestMessageReceiver(
    private val orderService: OrderService,
    private val messageSender: MessageSender
) {
    companion object : KLogging() {
        private const val BEER_BREWING_REQUEST_ROUTING_KEY = "brewing.requested"
    }

    @RabbitListener(queues = [MessagingConfiguration.QUEUE_NAME])
    fun receiveMessage(receivedMessage: OrderRequestedMessage, message: Message, channel: Channel) =
        processMessage(receivedMessage, message, channel) {
            val requestBrewingMessage = orderService.processOrderRequest(receivedMessage.beer)
            messageSender.sendMessage(requestBrewingMessage, BEER_BREWING_REQUEST_ROUTING_KEY)
        }

    protected fun processMessage(receivedMessage: OrderRequestedMessage, message: Message, channel: Channel, handleFunction: (OrderRequestedMessage) -> Unit) =
        withCorrelationId(message.messageProperties.headers[MessagingConfiguration.CORRELATION_HEADER_KEY]?.toString()) {
            logger.info("Received ${receivedMessage::class.simpleName} in ${this::class.simpleName}")
            handleFunction(receivedMessage)
            channel.basicAck(message.messageProperties.deliveryTag, false)
        }
}
