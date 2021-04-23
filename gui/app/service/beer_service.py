import logging
import threading
from typing import Callable

from PIL import Image
from pika import spec
from pika.adapters.blocking_connection import BlockingChannel

from app.beer_classifier.beer_classifier import BeerClassifier
from app.messaging import sender, receiver
from app.models.messages.order_requested_message import OrderRequestedMessage
from app.models.messages.serve_brewing_message import ServeBrewingMessage
from app.util.correlation_helper import set_or_generate_correlation_id

EXCHANGE_NAME = "beer-exchange"
QUEUE_NAME = "brewing-servings"
BEER_REQUESTED_ROUTING_KEY = "beer.requested"
BEER_SERVING_ROUTING_KEY = "beer.served"
CORRELATION_ID_HEADER_KEY = "BeerCorrelationId"


class BeerService:
    def __init__(self, finished_brewing_callback: Callable[[str], None]):
        self.beer_classifier = BeerClassifier()
        self.finished_brewing_callback = finished_brewing_callback

    def start_receiving(self) -> None:
        receiver_thread = threading.Thread(
            target=self.__start_receiving
        )
        receiver_thread.start()

    def upload_image(self, filename: str) -> None:
        with Image.open(filename) as image:
            beer_type = self.beer_classifier.classify_image(image)

            set_or_generate_correlation_id(None)

            logging.info("Send order request")

            sender.send_message(
                exchange_name=EXCHANGE_NAME,
                routing_key=BEER_REQUESTED_ROUTING_KEY,
                message=OrderRequestedMessage(
                    beer=beer_type
                )
            )

    def __received_served_brewing_callback(self, channel: BlockingChannel, method: spec.Basic.Deliver, properties: spec.BasicProperties, body: bytes) -> None:
        message: ServeBrewingMessage = ServeBrewingMessage.from_json(body)

        correlation_id: str = properties.headers.get(CORRELATION_ID_HEADER_KEY)
        set_or_generate_correlation_id(correlation_id)

        logging.info("Served a beer: %s", message.beer)
        logging.info("The brewing process was completed in %d seconds.", message.brewing_time)

        self.finished_brewing_callback(message.beer)

        channel.basic_ack(delivery_tag=method.delivery_tag)

    def __start_receiving(self) -> None:
        receiver.start_receiving(
            exchange_name=EXCHANGE_NAME,
            queue_name=QUEUE_NAME,
            routing_key=BEER_SERVING_ROUTING_KEY,
            callback=self.__received_served_brewing_callback
        )
