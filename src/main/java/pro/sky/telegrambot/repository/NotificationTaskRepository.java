package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.Collection;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    @Query(value = "SELECT * FROM notification_task WHERE date_time = :date_time", nativeQuery = true)
    public Collection<NotificationTask> getAllTasksByDateTime(@Param("date_time") LocalDateTime date_time);
}

