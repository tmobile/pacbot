from core.config import Settings


def need_to_deploy_vulnerability_service():
    feature_status = Settings.get('ENABLE_VULNERABILITY_FEATURE', False)

    return feature_status


def need_to_enable_azure():
    feature_status = Settings.get('ENABLE_AZURE', False)

    return feature_status
