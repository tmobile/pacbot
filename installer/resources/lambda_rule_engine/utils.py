from resources.iam.base_role import BaseRole
from resources.pacbot_app.utils import need_to_enable_azure
import json


def get_rule_engine_cloudwatch_rules_var():
    """
    Read cloudwatch rule details from the json file and build dict with required details

    Returns:
        variable_dict_input (list): List of dict of rule details used to generate terraform variable file
    """
    with open("resources/lambda_rule_engine/files/rule_engine_cloudwatch_rules.json", "r") as fp:
        data = fp.read()
    data = data.replace("role/pacman_ro", "role/" + BaseRole.get_input_attr('name'))

    variable_dict_input = json.loads(data)
    for index in range(len(variable_dict_input)):
        if not need_to_enable_azure and variable_dict_input['assetGroup'] == "azure":
            continue

        mod = index % 20 + 5
        item = {
            'ruleId': variable_dict_input[index]['ruleUUID'],
            'ruleParams': variable_dict_input[index]['ruleParams'],
            'schedule': "cron(%s * * * ? *)" % str(mod)
        }

        variable_dict_input[index] = item

    return variable_dict_input
