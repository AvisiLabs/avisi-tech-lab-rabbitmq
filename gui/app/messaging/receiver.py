from typing import Callable

from pika import spec
from pika.adapters.blocking_connection import BlockingChannel, BlockingConnection

from app.messaging import rabbit_connector


def start_receiving(exchange_name: str, queue_name: str, routing_key: str, callback: Callable[[BlockingChannel, spec.Basic.Deliver, spec.BasicProperties, bytes], None]) -> None:
    connection: BlockingConnection = rabbit_connector.get_connection()
    channel: BlockingChannel = connection.channel()

    rabbit_connector.exchange_declare(
        channel=channel,
        exchange_name=exchange_name
    )

    rabbit_connector.queue_declare(
        channel=channel,
        queue_name=queue_name
    )

    rabbit_connector.queue_bind(
        channel=channel,
        exchange_name=exchange_name,
        queue_name=queue_name,
        routing_key=routing_key
    )

    rabbit_connector.basic_consume(
        channel=channel,
        queue_name=queue_name,
        callback=callback
    )
