package ru.ifmo.acm.creepingline;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.Logger;
import ru.ifmo.acm.mainscreen.Utils;
import twitter4j.*;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class TwitterLoader extends Utils.StoppedRunnable {
    private static final Logger log = LogManager.getLogger(TwitterLoader.class);

    public static TwitterLoader getInstance() {
        if (instance == null) {
            instance = new TwitterLoader();
        }
        return instance;
    }

    private TwitterLoader() {
        Properties properties = new Properties();
        twitter = new TwitterFactory().getSingleton();
        try {
            properties.load(getClass().getResourceAsStream("/creepingline.properties"));
            account = properties.getProperty("twitter.account");
            duration = Long.parseLong(properties.getProperty("twitter.message.duration"));
            List<Status> updates = twitter.getUserTimeline(account);
            lastId = (updates.size() > 0) ? updates.get(0).getId() : -1;
        } catch (IOException | TwitterException e) {
            log.error("Twitter init failure", e);
        }
    }

    private List<Status> getUpdates() throws TwitterException{
        Paging paging = new Paging(lastId);
        List<Status> updates = (lastId < 0) ? twitter.getUserTimeline(account) : twitter.getUserTimeline(account, paging);

        if (updates.size() > 0){
            lastId = updates.get(0).getId();
        }
        return updates;
    }

    void postMessage(String message) {
        try {
            Status status = twitter.updateStatus(message);
        } catch (TwitterException e) {
            log.error("Twitter update failure", e);
        }
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                List<Status> updates = getUpdates();
                updates.forEach(tweet -> {
                    Message message = new Message(tweet.getText(), System.currentTimeMillis(), duration, false);
                    MessageData.getMessageData().addMessage(message);
                });
                Thread.sleep(60000);
            } catch (InterruptedException | TwitterException e) {
                log.error("Twitter get updates failure", e);
            }
        }
    }

    private String account;
    private long lastId;
    private final Twitter twitter;
    private long duration;
    private static TwitterLoader instance;
}
