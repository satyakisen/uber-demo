import com.google.inject.AbstractModule
import service.KafkaService
import service.impl.KafkaServiceImpl

class Module extends AbstractModule{
  override def configure() = {
    bind(classOf[KafkaService]).to(classOf[KafkaServiceImpl])
  }
}
