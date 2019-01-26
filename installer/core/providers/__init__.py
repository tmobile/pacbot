from core.config import Settings


class Provider:
    providers = ['AWS']
    valid = False

    def __init__(self, provider_name):
        if provider_name in self.providers:
            self.valid = True
            self.provider_module = "core.providers." + provider_name.lower()
