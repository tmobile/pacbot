resource "aws_s3_bucket" "pacbot-oss" {
  bucket = "${var.bucketname}-${var.accountid}"
  acl    = "private"
  tags {
    Name        = "${var.bucketname}-${var.accountid}"
    Environment = "Dev"
  }
}

resource "aws_s3_bucket_object" "folder" {
  bucket = "${aws_s3_bucket.pacbot-oss.bucket}"
  count  = "${length(var.folder_names)}"
  key    = "${var.folder_names[count.index]}/"
  source = "/dev/null"
  depends_on = ["aws_s3_bucket.pacbot-oss"]
}

resource "aws_s3_bucket_object" "s3_upload" {
  bucket = "${aws_s3_bucket.pacbot-oss.bucket}/"
  count  = "${length(var.filenames)}"
  key    = "${var.filenames[count.index]}"
  source = "upload/${var.filenames[count.index]}"
}
