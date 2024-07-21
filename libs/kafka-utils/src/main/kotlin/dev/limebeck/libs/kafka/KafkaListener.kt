package dev.limebeck.libs.kafka

import dev.limebeck.libs.logger.logger
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.*
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.Deserializer
import java.time.Duration
import java.util.*


data class KafkaListenerProperties<Key, Value>(
    val bootstrapServers: String,
    val keyDeserializer: Deserializer<Key>,
    val valueDeserializer: Deserializer<Value>,
    val groupId: String,
    val autoCommit: Boolean = false,
    val autoOffsetResetStrategy: OffsetResetStrategy = OffsetResetStrategy.EARLIEST,
    val properties: Properties = Properties()
)

fun KafkaListenerProperties<*, *>.toProperties() = Properties().apply {
    put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer::class.qualifiedName)
    put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer::class.qualifiedName)
    put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit.toString())
    put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetStrategy.toString())
    putAll(properties)
}

class KafkaListener<Key, Value>(
    private val topicNames: List<String>,
    private val properties: KafkaListenerProperties<Key, Value>,
    private val pollDuration: Duration = Duration.ofMillis(100),
    private val processor: suspend (ConsumerRecord<Key, Value>) -> Unit
) : AutoCloseable {
    companion object {
        private val logger = KafkaListener::class.logger()
    }

    private var keepConsume by atomic(true)

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    override fun close() {
        keepConsume = false
        logger.info { "<db7e542a> Stop consuming" }
    }

    suspend fun run() = coroutineScope.launch {
        KafkaConsumer(
            properties.toProperties(),
            properties.keyDeserializer,
            properties.valueDeserializer,
        ).use { consumer ->
            consumer.subscribe(topicNames)
            while (keepConsume) {
                val records = consumer.poll(pollDuration)
                logger.info { "<5dc10fe5> $consumer poll records: ${records.count()}" }

                for (r in records) {
                    processor(r)

                    if (!properties.autoCommit) {
                        consumer.commitSync(r.getMetadata())
                    }

                    if (!keepConsume)
                        break
                }
            }
        }
        logger.info { "<7284ebd2> Consumer closed" }
    }

    private fun ConsumerRecord<Key, Value>.getMetadata() = mapOf(
        TopicPartition(topic(), partition()) to OffsetAndMetadata(offset())
    )
}