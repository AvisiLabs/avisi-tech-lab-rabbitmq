import os
from typing import Callable

from pika import BlockingConnection, PlainCredentials, ConnectionParameters, BasicProperties, spec
from pika.adapters.blocking_connection import BlockingChannel
from pika.frame import Method

from app.models.messages.base_message import BaseMessage
from app.util.correlation_helper import get_correlation_id

PERSISTENT_MODE: int = 2
CORRELATION_ID_HEADER_KEY = "BeerCorrelationId"


def get_connection() -> BlockingConnection:
    return BlockingConnection(
        ConnectionParameters(
            host=os.getenv("RABBITMQ_HOST"),
            credentials=PlainCredentials(
                username=os.getenv("RABBITMQ_USERNAME"),
                password=os.getenv("RABBITMQ_PASSWORD")
            )
        )
    )


def exchange_declare(channel: BlockingChannel, exchange_name: str, exchange_type: str = "direct") -> None:
    channel.exchange_declare(
        exchange=exchange_name,
        exchange_type=exchange_type,
        durable=True
    )


def queue_declare(channel: BlockingChannel, queue_name: str = "") -> Method:
    return channel.queue_declare(
        queue=queue_name,
        durable=True,
        arguments={'x-queue-type': 'quorum'}
    )


def queue_bind(channel: BlockingChannel, exchange_name: str, queue_name: str, routing_key: str) -> None:
    channel.queue_bind(
        exchange=exchange_name,
        queue=queue_name,
        routing_key=routing_key
    )


def basic_publish(channel: BlockingChannel, exchange_name: str, routing_key: str, message: BaseMessage) -> None:
    body: str = message.to_json()
    correlation_id: str = get_correlation_id()

    properties = BasicProperties(
        delivery_mode=PERSISTENT_MODE,
        headers={CORRELATION_ID_HEADER_KEY: correlation_id}
    )

    channel.basic_publish(
        exchange=exchange_name,
        routing_key=routing_key,
        body=bytes(body, "utf-8"),
        properties=properties,
        mandatory=True
    )


def basic_consume(channel: BlockingChannel, queue_name: str, callback: Callable[[BlockingChannel, spec.Basic.Deliver, spec.BasicProperties, bytes], None]) -> None:
    channel.basic_consume(
        queue=queue_name,
        on_message_callback=callback
    )
    channel.start_consuming()
