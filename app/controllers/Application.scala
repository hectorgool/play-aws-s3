package controllers


import play.api._
import play.api.mvc._
import play.api.Play.current
import fly.play.s3._
import com.google.common.io.Files
import scala.concurrent.{Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class Application extends Controller {


	def index = Action {
		Ok(views.html.index("Your new application is ready."))
  	}

  	def s3Upload = Action {
		Ok(views.html.s3())
	}

	def submitS3Upload = Action.async(parse.multipartFormData) { request =>
		request.body.file("profile") match {
			case Some(profileImage) => {
				val bucketName = Play.current.configuration.getString("s3.bucketName").get
				val bucket = S3(bucketName)
				val filename = profileImage.filename
				val contentType = profileImage.contentType
				val byteArray = Files.toByteArray(profileImage.ref.file)
				val result = bucket.add(BucketFile(filename,contentType.get, byteArray, Option(PUBLIC_READ), None))
				println("ok:1")
				result.map { 
					unit =>
						println("ok:2")
				    	Logger.info("Saved the file")
				    	Ok("Image uploaded to: http://%s.s3.amazonaws.com/%s".format(bucketName, filename))
				}.recover {
					case S3Exception(status, code, message, originalXml) => 
						println("ok:3")
						Logger.info("Error: " + message)
						Ok("Error: " + message)
				}
			}
			case None => {
				println("ok:4")
				Future.successful(BadRequest)
			}
		}

	}


}