from resources.iam.base_role import BaseRole
import json


def get_rule_engine_cloudwatch_rules_var():
    with open("resources/lambda_rule_engine/files/rule_engine_cloudwatch_rules.json", "r") as fp:
        data = fp.read()
    data = data.replace("role/pacman_ro", "role/" + BaseRole.get_input_attr('name'))

    variable_dict_input = json.loads(data)
    for index in range(len(variable_dict_input)):
        mod = index % 10
        item = {
            'ruleId': variable_dict_input[index]['ruleUUID'],
            'ruleParams': variable_dict_input[index]['ruleParams'],
            'schedule': "cron(%s * * * ? *)" % str(mod)
        }

        variable_dict_input[index] = item

    return variable_dict_input
