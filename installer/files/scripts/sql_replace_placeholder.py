import os


def replace_placeholder_with_values(env_variables, sql_file):
    """
    Iterate over each line in the SQL file and replace any variables which is found in the
    env_variables dict so that all variables values get expanded with correct one

    Args:
        env_variables (dict): Dict containing only required env variables
        sql_file (str): File path where the SQL file is present
    """
    with open(sql_file, 'r') as f:
        lines = f.readlines()

    for idx, line in enumerate(lines):
        for key, value in env_variables.items():
            compare_str = "SET @%s='$%s'" % (key, key)
            if compare_str in line:
                replace = compare_str
                replace_with = "SET @%s='%s'" % (key, value)
                lines[idx] = line.replace(replace, replace_with)
                break

    with open(sql_file, 'w') as f:
        f.writelines(lines)


def get_env_variables_and_values(env_dict):
    """
    This method get all the environment variables which starts with ENV_ and create a dict with
    the key as the name after ENV_ and value as the environment value

    Args:
        env_dict (dict): This is the real enviorment variable dict

    Returns:
        env_variables (dict): Dict containing only required env variables
    """
    env_variables = {}
    for key, value in env_dict.items():
        if key.startswith('ENV_'):
            var_key = key.split('ENV_')[1]
            env_variables[var_key] = value

    return env_variables


if __name__ == "__main__":
    env_variables = get_env_variables_and_values(dict(os.environ.items()))
    sql_file = os.getenv('SQL_FILE_PATH')

    replace_placeholder_with_values(env_variables, sql_file)
