package service.impl

import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{Deserializer, Serializer, StringDeserializer, StringSerializer}
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import service.KafkaService

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.util.Try

@Singleton
class KafkaServiceImpl @Inject() (configuration: Configuration) extends KafkaService{


  private final val bootstrapServer: String = Try(configuration.underlying.getString("kafka.url"))
    .getOrElse("localhost:9092")

  def producerSettings: ProducerSettings[String, JsValue] = {
    val config: Config = Try(configuration.underlying.getConfig("akka.kafka.producer"))
      .getOrElse(ConfigFactory.empty())
    ProducerSettings[String, JsValue](config, new StringSerializer, new JsValueSerializer)
      .withBootstrapServers(bootstrapServer)
  }

  def consumerSettings: ConsumerSettings[String, JsValue] = {
    val config: Config = Try(configuration.underlying.getConfig("akka.kafka.consumer")).getOrElse(ConfigFactory.empty())
    ConsumerSettings[String, JsValue](config, new StringDeserializer, new JsValueDeserializer)
      .withBootstrapServers(bootstrapServer)
      .withGroupId(UUID.randomUUID().toString)
  }

  override def sink: Sink[ProducerRecord[String, JsValue], _] = {
    Producer.plainSink(producerSettings)
  }

  override def source(topic: String): Source[ConsumerRecord[String, JsValue], _] = {
    val subscription = Subscriptions.topics(topic)
    Consumer.plainSource(consumerSettings, subscription)
  }

  private class JsValueSerializer extends Serializer[JsValue] {
    override def serialize(topic: String, data: JsValue): Array[Byte] = data.toString().getBytes()
  }

  private class JsValueDeserializer extends Deserializer[JsValue] {
    override def deserialize(topic: String, data: Array[Byte]): JsValue = Json.parse(data)
  }
}
