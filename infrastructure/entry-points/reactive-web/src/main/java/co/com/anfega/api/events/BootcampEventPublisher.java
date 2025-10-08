package co.com.anfega.api.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class BootcampEventPublisher {

    private final SqsAsyncClient sqsClient = SqsAsyncClient.create();

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public Mono<Void> publishBootcampCreatedEvent(BootcampCreatedEvent event) throws BootcampEventSerializationException {
        String message = event.toJson();
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();

        CompletableFuture<Void> future = sqsClient.sendMessage(request)
                .thenAccept(response -> log.info("✅ Sent message ID: {}", response.messageId()));

        return Mono.fromFuture(future);
    }
}
