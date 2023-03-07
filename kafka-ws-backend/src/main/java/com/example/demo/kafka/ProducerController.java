package com.example.demo.kafka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.DocItem;
import com.example.demo.model.FileModel;
import com.example.demo.repository.DocumentRepository;

@RestController
@RequestMapping(value = "/api/kafka")
public class ProducerController {

	private static final Logger LOG = LoggerFactory.getLogger(ProducerController.class);

	private ProducerCallback producerCallback = new ProducerCallback();
	@Autowired
	private DocumentRepository repo;

	@Autowired
	private KafkaTemplate<String, FileModel> kafkaTemplate;

	@PostMapping(value = "/upload")
	public void handleFileUpload(@RequestPart("files[]") MultipartFile[] body) {
		IntStream.range(0, body.length)
				.peek(i -> this.waitFor(1))
				.mapToObj(i -> {
					try {
						FileModel fm = new FileModel(
								body[i].getOriginalFilename(),
								body[i].getBytes(),
								body[i].getContentType());
						return fm;
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				})
				.forEach(this::sendToKafka);

	}

	// Could be done without Mongo and with Kafka by generating a new unique group
	// ID resetting the offset of the topic, however this gives a lot of overhead in
	// Kafka.
	@RequestMapping(value = "/retrieve", method = RequestMethod.GET)
	public ArrayList<FileModel> generateMessages() {
		ArrayList<FileModel> fm = new ArrayList<FileModel>();
		List<DocItem> k = repo.findAll();
		for (DocItem item : k) {
			fm.add(item.getContent());
		}
		return fm;
	}

	private void sendToKafka(FileModel model) {
		String uid = UUID.randomUUID().toString();
		DocItem temp = new DocItem(uid, model);
		repo.save(temp); // Save item in DB as well so that the message persists while the consumer
							// offset shifts in Kafka
		this.kafkaTemplate
				.send(model.getRawFormat(), uid, model)
				.addCallback(this.producerCallback);
	}

	private void waitFor(int seconds) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		Future<Void> future = scheduler.schedule(() -> null, seconds, TimeUnit.SECONDS);
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	class ProducerCallback implements ListenableFutureCallback<SendResult<String, FileModel>> {
		@Override
		public void onSuccess(@Nullable SendResult <String, FileModel> result) {
			if(result != null){
			RecordMetadata record = result.getRecordMetadata();
			LOG.info("Sending {} to topic {} - partition {}",
					result.getProducerRecord().key(),
					result.getProducerRecord().topic(),
					record.partition());
			}
		}

		@Override
		public void onFailure(Throwable ex) {
			LOG.error("Producer Failure", ex);
		}
	}

}
