import kafka.server.{KafkaConfig, KafkaServer}
import org.apache.zookeeper.server.{ServerConfig, ZooKeeperServerMain}
import org.apache.zookeeper.server.quorum.QuorumPeerConfig

import java.io.File
import java.net.InetSocketAddress
import java.nio.file.{Files, Paths}
import java.util.Properties

/**
 * Kafka Server Application object to run a single node kafka server
 */
object KafkaServerApp extends App{

  // Loads the application.properties file which contains few zookeeper configurations
  val resource = getClass.getClassLoader.getResourceAsStream("application.properties")
  val prop = new Properties()
  prop.load(resource)


  //This is the root tmp folder path. The code is build on linux os, so considering /tmp to exist.
  val tmpPath = Paths.get("/tmp").toRealPath()

  val zkDataDir = Files.createTempDirectory(tmpPath, prop.getProperty("zookeeper.datadir", "zookeeper"))
  val zkLogDataDir = Files.createTempDirectory(tmpPath, prop.getProperty("zookeeper.data.logdir", "zookeeper-logs"))
  val inetSocketAddress = new InetSocketAddress(prop.getProperty("zookeeper.client.port", "2181").toInt)

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

  //Starting a zookeeper service on a thread
  zkThread.start()


  val kfLogDir = Files.createTempDirectory(tmpPath, "kafka-logs")

  val kafkaProperties = new Properties()
  kafkaProperties.setProperty("zookeeper.connect", "localhost:" + prop.getProperty("zookeeper.client.port", "2181"))
  kafkaProperties.setProperty("broker.id", "1")
  kafkaProperties.setProperty("log.dirs", kfLogDir.toString)

  val kafkaConfig = KafkaConfig.fromProps(kafkaProperties)
  val kafka = new KafkaServer(kafkaConfig)

  // Starting the kafka service on main thread
  kafka.startup()

  // waiting zookeeper thread till the main thread notifies of its startup
  zkThread.join()

  kafka.shutdown()
  kafka.awaitShutdown()

  zkServer.stop()
}
