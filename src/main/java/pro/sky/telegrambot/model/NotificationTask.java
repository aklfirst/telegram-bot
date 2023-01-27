package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long chat_id;
    private String message;
    private LocalDateTime date_time;

    public NotificationTask(long chat_id, String message, LocalDateTime date_time) {
        this.chat_id = chat_id;
        this.message = message;
        this.date_time = date_time;
    }

    public NotificationTask() {

    }

    public long getId() {
        return id;
    }

    public long getChat_id() {
        return chat_id;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate_time() {
        return date_time;
    }

    public void setChat_id(long chat_id) {
        this.chat_id = chat_id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate_time(LocalDateTime date_time) {
        this.date_time = date_time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}





