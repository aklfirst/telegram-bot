package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;


    private final String welcomeMessage = "Welcome to AKL telegram chat bot!";
    private final String notificationPattern = "([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)";


    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() == null) {
                return;
            }

            String inputMessageText = update.message().text();
            long chatId = update.message().chat().id();

            // Process welcome message if "/start" command input message text is received
            if (inputMessageText.equals("/start")) {
                sendMessage(chatId, welcomeMessage);
            }

            // Process notification task message if other commands received

            else {
                Pattern pattern = Pattern.compile(notificationPattern);
                Matcher matcher = pattern.matcher(inputMessageText);
                if (matcher.matches()) {
                    String date = matcher.group(1);
                    String message = matcher.group(3);
                    LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    notificationTaskRepository.save(new NotificationTask(chatId, message, localDateTime));
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
        Iterator<NotificationTask> iterator = currentTasks.iterator();
        while (iterator.hasNext()) {
            NotificationTask task = iterator.next();
            sendMessage(task.getChat_id(), task.getMessage());
        }
    }


    public void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId, messageText);
        SendResponse response = telegramBot.execute(message);
        if (!response.isOk()) {
            logger.warn("Message was not sent: {}, error: {}", message, response.errorCode());
        }
    }

}
