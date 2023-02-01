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
    private long chatId;
    private String message;
    private LocalDateTime dateTime;

    public NotificationTask(long chatId, String message, LocalDateTime dateTime) {
        this.chatId = chatId;
        this.message = message;
        this.dateTime = dateTime;
    }

    public NotificationTask() {

    }

    public long getId() {
        return id;
    }

    public long getChatId() {
        return chatId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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





