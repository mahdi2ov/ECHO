package echo.security.validation.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import echo.config.AppConfig;
import echo.security.validation.MessageValidationRule;

public class SpamRateRule implements MessageValidationRule {
    // cycle for checking spam messages
    private static final long CYCLE_IN_MILLIES = 1000;
    private final Map<String, List<Long>> userMessagesCycles;
    
    // constructor
    public SpamRateRule() {
        this.userMessagesCycles = new HashMap<>();
    }

    // check message spam rate and save message if it is ok
    @Override
    public synchronized String validate(String message, String senderId) {
        long currentTime = System.currentTimeMillis();
        List<Long> userMessageTimes = this.userMessagesCycles.get(senderId);
        if (userMessageTimes == null) {
            userMessageTimes = new ArrayList<>();
            userMessagesCycles.put(senderId, userMessageTimes);
        }
        Iterator<Long> iterator = userMessageTimes.iterator();
        while (iterator.hasNext()) {
            if (currentTime - iterator.next() >= CYCLE_IN_MILLIES) {
                iterator.remove();
            }
        }
        if (userMessageTimes.size() >= AppConfig.getMaxMessagePerSecond()) {
            return "You must not send more than " + AppConfig.getMaxMessagePerSecond() + " messages per second.";
        }
        userMessageTimes.add(currentTime);
        return null;
    }
}
