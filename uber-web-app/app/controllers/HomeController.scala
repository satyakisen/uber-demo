package controllers

import akka.stream.scaladsl.Flow
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.libs.json.JsValue

import javax.inject._
import play.api.mvc._
import service.KafkaService

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val kafkaService: KafkaService, val controllerComponents: ControllerComponents) extends BaseController {

  private val mapAccessToken: String = "pk.eyJ1Ijoic2F0eWFraTciLCJhIjoiY2txM2RxNnBpMHhoNzJ3czUwOHg1aDFzNCJ9.zr3B990jstLmw_MTRQ0y1A"
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def driver() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.driver(routes.HomeController.driverWs.webSocketURL(), mapAccessToken))
  }


  def rider() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.rider(routes.HomeController.riderWs.webSocketURL(), mapAccessToken))
  }

  def driverWs = WebSocket.accept { _ =>
    val sink = kafkaService.sink.contramap[JsValue]( new ProducerRecord("driver", "", _))
    val source = kafkaService.source("rider").map(_.value())
    Flow.fromSinkAndSource(sink, source)
  }

  def riderWs = WebSocket.accept { _ =>
    val sink = kafkaService.sink.contramap[JsValue](new ProducerRecord("rider", "", _))
    val source = kafkaService.source("driver").map(_.value())
    Flow.fromSinkAndSource(sink, source)
  }
}
