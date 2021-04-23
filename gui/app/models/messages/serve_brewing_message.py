from dataclasses import dataclass, field

from dataclasses_json import dataclass_json, config

from app.models.messages.base_message import BaseMessage


@dataclass_json
@dataclass
class ServeBrewingMessage(BaseMessage):
    beer: str = field(metadata=config(field_name="beer"))
    brewing_time: int = field(metadata=config(field_name="brewingTime"))
