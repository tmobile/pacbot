import os


def replace_placeholder_with_values(aws_region, aws_account_id, es_host, es_port, sql_file):
    with open(sql_file, 'r') as f:
        lines = f.readlines()

    for idx, line in enumerate(lines):
        if "SET @region='$region';" in line:
            lines[idx] = line.replace("@region='$region'", "@region='" + aws_region + "'")
        if "SET @account='$account';" in line:
            lines[idx] = line.replace("@account='$account'", "@account='" + aws_account_id + "'")
        if "SET @eshost='$eshost';" in line:
            lines[idx] = line.replace("@eshost='$eshost'", "@eshost='" + es_host + "'")
        if "SET @esport='$esport';" in line:
            lines[idx] = line.replace("@esport='$esport'", "@esport='" + es_port + "'")

    with open(sql_file, 'w') as f:
        f.writelines(lines)


if __name__ == "__main__":
    aws_region = os.getenv('AWS_REGION')
    aws_account_id = os.getenv('AWS_ACCOUNT_ID')
    es_host = os.getenv('ES_HOST')
    es_port = os.getenv('ES_PORT')
    sql_file = os.getenv('SQL_FILE_PATH')

    replace_placeholder_with_values(aws_region, aws_account_id, es_host, es_port, sql_file)
