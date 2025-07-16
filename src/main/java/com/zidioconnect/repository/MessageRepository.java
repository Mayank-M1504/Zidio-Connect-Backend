package com.zidioconnect.repository;

import com.zidioconnect.model.Message;
import com.zidioconnect.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByApplicationOrderByTimestampAsc(Application application);
}