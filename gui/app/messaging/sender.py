import pika.exceptions
import logging
from pika import BlockingConnection
from pika.adapters.blocking_connection import BlockingChannel

from app.messaging import rabbit_connector
from app.models.messages.base_message import BaseMessage

APPLICATION_NAME = "beer-gui"


def send_message(exchange_name: str, routing_key: str, message: BaseMessage) -> None:
    connection: BlockingConnection = rabbit_connector.get_connection()
    channel: BlockingChannel = connection.channel()
    channel.confirm_delivery()

    rabbit_connector.exchange_declare(
        channel=channel,
        exchange_name=exchange_name
    )

    try:
        rabbit_connector.basic_publish(
            channel=channel,
            exchange_name=exchange_name,
            routing_key=routing_key,
            message=message
        )
        connection.close()
    except pika.exceptions.UnroutableError as e:
        logging.error("Failed to send message: ", e)
        connection.close()
