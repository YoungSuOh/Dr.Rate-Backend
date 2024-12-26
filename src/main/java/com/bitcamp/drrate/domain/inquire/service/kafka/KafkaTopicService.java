package com.bitcamp.drrate.domain.inquire.service.kafka;

import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.InquireServiceHandler;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownServerException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class KafkaTopicService {
    private final KafkaAdmin kafkaAdmin;
    private final AdminClient adminClient;

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
    public boolean topicExists(String topicName) {
        try {
            System.out.println("topicExists");
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singletonList(topicName));
            result.values().get(topicName).get(); // 토픽이 없으면 예외 발생
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}