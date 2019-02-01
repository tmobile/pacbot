from core.terraform.resources import BaseTerraformVariable


class TerraformVariable(BaseTerraformVariable):
    variable_dict_input = None
    variable_type = None
    available_args = {
        'variable_name': {'required': True},
        'variable_type': {'required': False},
        'default_value': {'required': False}
    }
