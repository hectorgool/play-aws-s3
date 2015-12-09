import os
import boto
import boto.s3.connection
access_key = os.environ["AWS_ACCESS_KEY"]
secret_key = os.environ["AWS_SECRET_KEY"]

#print(access_key)
#print(secret_key)

conn = boto.connect_s3(
	aws_access_key_id = access_key,
    aws_secret_access_key = secret_key,
    host = 'objects.dreamhost.com',
    #is_secure=False,               # uncomment if you are not using ssl
    calling_format = boto.s3.connection.OrdinaryCallingFormat(),
)

for bucket in conn.get_all_buckets():
	print ("{name}\t{created}").format(
		name = bucket.name, 
		created = bucket.creation_date
		)