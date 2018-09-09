package fr.mbutton.prometheus;

import java.util.Arrays;
import java.util.Properties;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class KafkaListener implements ServletContextListener {

	private transient Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Starting Kafka Listener");
		Runnable listener = () -> {
			Properties props = new Properties();
			props.put("bootstrap.servers" , "localhost:9092");
			props.put("group.id"          , "confluent");
			props.put("key.deserializer"  , "org.apache.kafka.common.serialization.StringDeserializer");
			props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

			try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
				consumer.subscribe(Arrays.asList("sensor.counter"));

				while (true) {
					ConsumerRecords<String, String> records = consumer.poll(10);
					for (ConsumerRecord<String, String> record : records) {
						KafkaCounterExporter.counterValue = record.value();
					}
				}
			}
		};

		Thread t = new Thread(listener);
		t.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("Disconnecting");
	}
}
