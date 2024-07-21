package dev.limebeck.libs.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serializer
import java.util.*

data class KafkaProducerProperties<Key, Value>(
    val bootstrapServers: String,
    val keySerializer: Serializer<Key>,
    val valueSerializer: Serializer<Value>,
    val properties: Properties = Properties()
)

fun KafkaProducerProperties<*, *>.toProperties() = Properties().apply {
    put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer::class.qualifiedName)
    put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer::class.qualifiedName)
    putAll(properties)
}

fun <Key, Value> KafkaProducer(
    properties: KafkaProducerProperties<Key, Value>,
) = KafkaProducer<Key, Value>(
    properties.toProperties(),
    properties.keySerializer,
    properties.valueSerializer,
)