import logging

from app.ui.user_interface import UserInterface


def main():
    logging.basicConfig()
    logging.getLogger().setLevel(logging.INFO)
    logging.getLogger("pika").setLevel(logging.WARNING)

    interface = UserInterface()
    interface.show()


if __name__ == "__main__":
    main()
