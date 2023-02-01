package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;


    private static final String WELCOME_MESSAGE = "Welcome to AKL telegram chat bot!";
    private static final String NOTIFICATION_PATTERN = "([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            String inputMessageText = update.message().text();
            long chatId = update.message().chat().id();

            // Process welcome message if "/start" command input message text is received
            if ("/start".equals(inputMessageText)) {
                sendMessage(chatId, WELCOME_MESSAGE);
            }

            // Process notification task message if other commands received

            else {
                Pattern pattern = Pattern.compile(NOTIFICATION_PATTERN);
                Matcher matcher = pattern.matcher(inputMessageText);
                LocalDateTime dateTime;
                if (matcher.matches() && (dateTime = parse(matcher.group(1))) != null) {
                    String message = matcher.group(3);
                    notificationTaskRepository.save(new NotificationTask(chatId, message, dateTime));
                    sendMessage(chatId, "Task planned!");
                } else {
                    sendMessage(chatId, "wrong message format!");
                }
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    //Activate Scheduling

    // to run at 00 sec every minute
    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotificationTasks() {
        Collection<NotificationTask> currentTasks = notificationTaskRepository.getAllTasksByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        currentTasks.forEach(task -> sendMessage(task.getChatId(), task.getMessage()));
    }


    public void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId, messageText);
        SendResponse response = telegramBot.execute(message);
        if (!response.isOk()) {
            logger.warn("Message was not sent: {}, error: {}", message, response.errorCode());
        }
    }

    @Nullable
    private LocalDateTime parse(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }

    }

}
