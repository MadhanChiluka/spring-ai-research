package com.tech.springai.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;


@Component
public class TimeTools {

    private static final Logger logger = LoggerFactory.getLogger(TimeTools.class);

    @Tool(name = "getLocalDateTime", description = "Get Time and date for user's location")
    public LocalDateTime getLocalDateTime() {
        logger.info("Getting Local Time and Date for user's Location");
        return LocalDateTime.now();
    }

    @Tool(name = "getLocalTime", description = "Get the current time in the specified time zone")
    public String getTimeZone(@ToolParam(description = "Value representing the time zone") String timeZone) {
        logger.info("Returning the current time int the timezone {}", timeZone);
        return LocalTime.now(ZoneId.of(timeZone)).toString();
    }
}
