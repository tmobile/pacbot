resource "aws_s3_bucket_object" "s3_upload" {
  bucket = "${var.bucketname}-${var.accountid}"
  count  = "${length(var.filenames)}"
  key    = "${var.filenames[count.index]}"
  source = "upload/${var.filenames[count.index]}"
}
