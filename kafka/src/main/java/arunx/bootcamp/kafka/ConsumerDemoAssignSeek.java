package arunx.bootcamp.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class ConsumerDemoAssignSeek 
{
	public static void main( String[] args ) throws InterruptedException, ExecutionException {
		final Logger log = LoggerFactory.getLogger(ConsumerDemoAssignSeek.class);

		String bootstrapServers = "127.0.0.1:9092, 127.0.0.1:9090";
		String topic = "first_topic";
		
		//TODO: create consumer configs
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		//TODO: create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
		
		// assign and seek are mostly used to replay data or fetch a specific message
		// assign
		TopicPartition partitionToReadFrom = new TopicPartition(topic, 0);
		Long offsetToReadFrom = 15L;
		consumer.assign(Arrays.asList(partitionToReadFrom));
		
		// seek
		consumer.seek(partitionToReadFrom, offsetToReadFrom);
		
		int numberOfMessagesToRead = 5;
		boolean keepOnReading = true;
		int numberOfMessagesReadSoFar = 0;
		
		//TODO: poll for new data
		while(keepOnReading) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				numberOfMessagesReadSoFar++;
				log.info("Key: " + record.key() + ", Value: " + record.value());
				log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
				if (numberOfMessagesReadSoFar >= numberOfMessagesToRead) {
					keepOnReading = false;
					break; // to exit the for loop
				}
			}
		}
		log.info("Exiting the application");
	}
}
