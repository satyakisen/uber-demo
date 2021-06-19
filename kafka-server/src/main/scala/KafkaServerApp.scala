import kafka.server.{KafkaConfig, KafkaServer}
import org.apache.zookeeper.server.{ServerConfig, ZooKeeperServerMain}
import org.apache.zookeeper.server.quorum.QuorumPeerConfig

import java.io.File
import java.net.InetSocketAddress
import java.nio.file.{Files, Paths}
import java.util.Properties
object KafkaServerApp extends App{

  val resource = getClass.getClassLoader.getResourceAsStream("application.properties")
  val prop = new Properties()
  prop.load(resource)


  val tmpPath = Paths.get("/tmp").toRealPath()
  val zkDataDir = Files.createTempDirectory(tmpPath,
    prop.getProperty("zookeeper.datadir", "zookeeper"))
  val zkLogDataDir = Files.createTempDirectory(tmpPath,
    prop.getProperty("zookeeper.data.logdir", "zookeeper-logs"))
  val inetSocketAddress = new InetSocketAddress(
    prop.getProperty("zookeeper.client.port", "2181").toInt)

  val quorumPeerConfig = new QuorumPeerConfig{
    override def getDataLogDir: File = zkLogDataDir.toFile
    override def getDataDir: File = zkDataDir.toFile
    override def getClientPortAddress: InetSocketAddress = inetSocketAddress
  }

  class ZkServerMain extends ZooKeeperServerMain{
    def stop():Unit = shutdown()
  }
  val zkServer = new ZkServerMain()

  val zkServerConfig = new ServerConfig()
  zkServerConfig.readFrom(quorumPeerConfig)

  val zkThread = new Thread{
    override def run(): Unit = {
      zkServer.runFromConfig(zkServerConfig)
    }
  }

  zkThread.start()

  val kfLogDir = Files.createTempDirectory(tmpPath, "kafka-logs")
  val kafkaProperties = new Properties()
  kafkaProperties.setProperty("zookeeper.connect",
    "localhost:" + prop.getProperty("zookeeper.client.port", "2181"))
  kafkaProperties.setProperty("broker.id", "1")
  kafkaProperties.setProperty("log.dirs", kfLogDir.toString)


  val kafkaConfig = KafkaConfig.fromProps(kafkaProperties)
  val kafka = new KafkaServer(kafkaConfig)

  kafka.startup()
  zkThread.join()

  kafka.shutdown()
  kafka.awaitShutdown()

  zkServer.stop()
}
