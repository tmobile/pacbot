from core.terraform.resources import BaseTerraformVariable


class TerraformVariable(BaseTerraformVariable):
    """
    Base resource class for Terraform tfvar variable

    Attributes:
        variable_dict_input (dict/none): Var dict values
        available_args (dict): Instance configurations
        variable_type (str): Define the variable i.e. terraform list var or terraform dict var etc
    """
    variable_dict_input = None
    variable_type = None
    available_args = {
        'variable_name': {'required': True},
        'variable_type': {'required': False},
        'default_value': {'required': False}
    }
