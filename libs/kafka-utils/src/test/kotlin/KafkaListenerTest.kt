import dev.limebeck.libs.kafka.*
import dev.limebeck.libs.logger.logger
import dev.limebeck.testUtils.awaitAssertWithDelay
import dev.limebeck.testUtils.runTest
import kotlinx.atomicfu.atomic
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.*
import kotlin.test.*

class KafkaListenerTest {
    private val logger = KafkaListenerTest::class.logger()

    private lateinit var kafka: KafkaContainer

    @BeforeTest
    fun before() {
        kafka = KafkaContainer(DockerImageName.parse("apache/kafka:3.7.1"))
            .apply {
                start()
            }
    }

    @AfterTest
    fun after() {
        kotlin.runCatching { kafka.close() }
    }

    private val consumerProperties
        get() = KafkaListenerProperties(
            bootstrapServers = kafka.bootstrapServers,
            keyDeserializer = StringDeserializer(),
            valueDeserializer = StringDeserializer(),
            groupId = "test-group-id",
        )

    private fun createListener(
        topic: String,
        processor: suspend (ConsumerRecord<String, String>) -> Unit
    ) = KafkaListener(
        topicNames = listOf(topic),
        properties = consumerProperties,
        processor = processor
    )

    private fun useProducer(block: (producer: KafkaProducer<String, String>) -> Unit) {
        val producerProperties = KafkaProducerProperties(
            bootstrapServers = kafka.bootstrapServers,
            keySerializer = StringSerializer(),
            valueSerializer = StringSerializer(),
        )
        KafkaProducer(producerProperties).use { block(it) }
    }

    private fun useAdmin(block: (admin: AdminClient) -> Unit) {
        val properties = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.bootstrapServers)
        }
        KafkaAdminClient.create(properties).use { block(it) }
    }

    @Test
    fun `Listen to all records`() = runTest {
        val testTopic = "test_topic"
        val expectedMessagesCount = 10
        val receivedRecordsCount = atomic(0)

        useAdmin { admin ->
            admin.createTopics(
                listOf(
                    NewTopic(
                        /* name = */ testTopic,
                        /* numPartitions = */ 1,
                        /* replicationFactor = */ 1
                    )
                )
            )
        }

        val listener = createListener(testTopic) { r ->
            receivedRecordsCount.incrementAndGet()
        }

        logger.info { "<504dd9a0> Before run listener" }
        val listenerJob = listener.run()
        logger.info { "<4a0783e7> After run listener" }

        useProducer { producer ->
            repeat(expectedMessagesCount) {
                producer.send(ProducerRecord(testTopic, it.toString(), it.toString()))
            }
        }

        awaitAssertWithDelay {
            assertEquals(receivedRecordsCount.value, 10)
        }

        listener.close()

        listenerJob.join()
        assert(listenerJob.isCompleted)
    }

    @Test
    fun `Error handling test`() = runTest {
        val testTopic = "test_topic"
        val expectedMessagesCount = 10
        val receivedRecordsCount = atomic(0)

        useAdmin { admin ->
            admin.createTopics(
                listOf(
                    NewTopic(
                        /* name = */ testTopic,
                        /* numPartitions = */ 1,
                        /* replicationFactor = */ 1
                    )
                )
            )
        }

        val listener = createListener(testTopic) { r ->
            if (r.value() == "2") {
                throw RuntimeException("<9bb27c68> Error")
            }
            receivedRecordsCount.incrementAndGet()
        }

        logger.info { "<504dd9a0> Before run listener" }
        val listenerJob = listener.run()
        logger.info { "<4a0783e7> After run listener" }

        useProducer { producer ->
            repeat(expectedMessagesCount) {
                producer.send(ProducerRecord(testTopic, it.toString(), it.toString()))
            }
        }

        listenerJob.invokeOnCompletion {
            assertIs<RuntimeException>(it)
        }
        listenerJob.join()
        awaitAssertWithDelay {
            assert(listenerJob.isCancelled)
        }
    }
}

