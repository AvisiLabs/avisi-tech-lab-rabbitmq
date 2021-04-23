import os


def get_env_var_as_string(key: str) -> str:
    return str(os.getenv(key))
