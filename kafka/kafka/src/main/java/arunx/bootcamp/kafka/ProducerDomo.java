package arunx.bootcamp.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class ProducerDomo 
{
	public static void main( String[] args ) throws InterruptedException, ExecutionException {
		final Logger log = LoggerFactory.getLogger(ProducerDomo.class);
		String bootstrapServers = "127.0.0.1:9092";

		//TODO: create producer properties
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		//TODO: create the producer
		KafkaProducer<String, String>	producer = new KafkaProducer<String, String>(properties);
		
		for (int i = 0; i < 10; i++) {
			//TODO: create producer record
			String topic = "first_topic";
			String value = "hello world " + i;
			String key = "id_" + i;
			ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, value);
			
			log.info("Key: " + key);

			//TODO: send data - asynchromous
			//producer.send(record);
			producer.send(record, new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					// executes everytime a record is successfully sent or an exception is thrown
					if (e == null) {
						log.info("Received new metadata. \n" +
											"Topic: " + metadata.topic() + "\n" +
											"Partition: " + metadata.partition() + "\n" + 
											"Offset: " + metadata.offset() + "\n" + 
											"Timestamp: " + metadata.timestamp());
					} else {
						log.error("Error while producing", e);
					}
				}
			}).get(); // block the .send() to make it synchronous - don't do this in production!
		}
		
		// flush data
		producer.flush();
		
		// flush and close producer
		producer.close();
	}
}
