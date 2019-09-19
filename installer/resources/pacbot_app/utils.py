from core.config import Settings


def need_to_deploy_vulnerability_service():
    qualys_api_base_url = Settings.get('QUALYS_API_BASE_URL', None)
    qualys_api_base_url = qualys_api_base_url if qualys_api_base_url else None

    return bool(qualys_api_base_url)
