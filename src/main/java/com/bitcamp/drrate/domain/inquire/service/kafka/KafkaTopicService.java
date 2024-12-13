package com.bitcamp.drrate.domain.inquire.service.kafka;

import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.InquireServiceHandler;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownServerException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTopicService {
    private final KafkaAdmin kafkaAdmin;

    public void createTopic(String topicName) {
        try {
            // 토픽 생성
            NewTopic topic = new NewTopic(topicName, 1, (short) 1);
            kafkaAdmin.createOrModifyTopics(topic);
            System.out.println("Kafka Topic Created: " + topicName);
        } catch (IllegalArgumentException e) {
            throw new InquireServiceHandler(ErrorStatus.KAFKA_TOPIC_CREATE_BADREQUEST);
        } catch (TopicExistsException e) {
            throw new InquireServiceHandler(ErrorStatus.KAFKA_TOPIC_EXIST_ERROR);
        } catch (KafkaException e) {
            throw new InquireServiceHandler(ErrorStatus.KAFKA_BROKER_BADREQUEST);
        } catch (UnknownServerException e) {
            throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new InquireServiceHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}