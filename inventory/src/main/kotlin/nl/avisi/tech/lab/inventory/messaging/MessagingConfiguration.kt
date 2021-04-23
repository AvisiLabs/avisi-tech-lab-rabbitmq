package nl.avisi.tech.lab.inventory.messaging

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class MessagingConfiguration {

    companion object {
        const val EXCHANGE_NAME = "beer-exchange"
        const val QUEUE_NAME = "beer-requests"
        const val BEER_REQUEST_ROUTING_KEY = "beer.requested"
        const val CORRELATION_HEADER_KEY = "correlation-id"
    }

    @Bean
    fun exchange() = DirectExchange(EXCHANGE_NAME)

    @Bean
    fun beerRequestQueue(): Queue = QueueBuilder.durable(QUEUE_NAME).build()

    @Bean
    fun beerRequestBinding(beerRequestQueue: Queue, exchange: DirectExchange): Binding =
        BindingBuilder
            .bind(beerRequestQueue)
            .to(exchange)
            .with(BEER_REQUEST_ROUTING_KEY)

    @Bean
    fun messageSender(
        rabbitTemplate: RabbitTemplate,
        @Value("\${spring.application.name}") applicationName: String
    ) = MessageSender(rabbitTemplate, applicationName)

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        jackson2JsonMessageConverter: Jackson2JsonMessageConverter
    ): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = jackson2JsonMessageConverter
        rabbitTemplate.setMandatory(true)
        return rabbitTemplate
    }

    @Bean
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder =
        Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule())

    @Bean
    fun producerJackson2MessageConverter(objectMapperBuilder: Jackson2ObjectMapperBuilder): Jackson2JsonMessageConverter =
        Jackson2JsonMessageConverter(objectMapperBuilder.build())
}
