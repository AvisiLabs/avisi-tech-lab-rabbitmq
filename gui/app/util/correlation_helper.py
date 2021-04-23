import threading
import uuid
from typing import Optional

thread_local_storage = threading.local()


def set_or_generate_correlation_id(correlation_id: Optional[str]) -> None:
    if correlation_id is None:
        correlation_id = str(uuid.uuid4())
    thread_local_storage.correlation_id = correlation_id


def get_correlation_id() -> str:
    return thread_local_storage.correlation_id


def clear_correlation_id() -> None:
    thread_local_storage.correlation_id = None
