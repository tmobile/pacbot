#! /usr/bin/env python
import json
import jsonRead
import time
filename = 'terraform_input.json'
with open(filename, 'r') as data_file:
    data = json.load(data_file)


def _get_variable_list(key):
    return data['resources'][key]


def _get_method_list(key):
    return data[key]['methods']


def _get_execution_order():
    return data['execution_order']


def _get_terraform_map(key, setup):
    aws_data = {}
    try:
        variables = data['resources'][key].keys()
        for variable in variables:
            try:
                methodname = data['resources'][key][variable]
                value = ""
                if setup == "destroy" and variable == "build-ui":
                    continue
                if len(methodname) > 1:
                    value = getattr(jsonRead, methodname)()
                aws_data.update({variable: value})
            except TypeError as te:
                if methodname == 1:
                    aws_data.update({variable: methodname})
    except KeyError as ke:
        if key == "refresh":
            print "Waiting for the role to refresh"
            time.sleep(15)
            return None

    return aws_data


def _get_rules_json():
    rulesfilename = 'rule_engin_cloudwatch_rule.json'
    outputfilename = 'rule_engin_cloudwatch_rule_output.json'
    with open(rulesfilename, 'r') as data_file:
        data = json.load(data_file)
    newdata = []
    for rule in data:
        value = rule['ruleParams']
        value = re.escape(value)
        rule.update({'ruleParams': value})
        newdata.append(rule)
    with open(outputfilename, 'w') as data_file:
        data_file.write(json.dumps(newdata, sort_keys=True, indent=4, separators=(',', ': ')))
