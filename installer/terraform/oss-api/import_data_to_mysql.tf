/*
 This would load the mysql script provided in the DB.sql file to pacmandata Database
*/
resource "null_resource" "load_mysql_schema" {
  provisioner "local-exec" {
       command = "mysql -u ${var.RDS_USERNAME} -p${var.RDS_PASSWORD} -h ${var.RDS_HOST_ENDPOINT} < DB_With_Values.sql"
  }
}
