package controllers


import play.api._
import play.api.mvc._
import play.api.Play.current
import fly.play.s3._


class Application extends Controller {


	def index = Action {
		Ok(views.html.index("Your new application is ready."))
  	}

  	def s3Upload = Action {
		Ok(s3())
	}

	def submitS3Upload = Action(parse.multipartFormData) {
		request =>
		//import play.api.Play
		request.body.file("profile") match {
			case Some(profileImage) => {
				val bucketName = Play.current.configuration.getString("s3.bucketName").get
				val bucket = S3(bucketName)
				val filename = profileImage.filename
				val contentType = profileImage.contentType
				val byteArray = Files.toByteArray(profileImage.ref.file)
				val result = bucket.add(BucketFile(filename,contentType.get, byteArray, Option(PUBLIC_READ), None))
				val future = Await.result(result, 10 seconds)
				Ok("Image uploaded to: http://%s.s3.amazonaws.com/%s".format(bucketName, filename))
			}
			case None => {
				BadRequest
			}
		}

	}


}