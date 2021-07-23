package service

import akka.stream.scaladsl.{Sink, Source}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.libs.json.JsValue

trait KafkaService {
  def sink : Sink[ProducerRecord[String, JsValue], _]
  def source(topic: String) : Source[ConsumerRecord[String, JsValue], _]
}
