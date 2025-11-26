package com.tech.springai.tools;

import com.tech.springai.entity.HelpDeskTicket;
import com.tech.springai.model.TicketRequest;
import com.tech.springai.service.HelpDeskTicketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HelpDeskTools {
    private static final Logger logger = LoggerFactory.getLogger(HelpDeskTools.class);

    private final HelpDeskTicketService helpDeskTicketService;

    @Tool(name = "createTicket", description = "Create the Support Ticket", returnDirect = true)
    public String createTicket(@ToolParam(description = "Details to create ticket") TicketRequest ticketRequest, ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");
        logger.info("Creating support ticket for user: {} with details: {}", username, ticketRequest);
        HelpDeskTicket savedTicket = helpDeskTicketService.createTicket(ticketRequest, username);
        logger.info("Ticket created successfully, Ticket ID: {}, Username {}", savedTicket.getId(), savedTicket.getUsername());
        return "Ticket #" + savedTicket.getId() + " created successfully for user " + savedTicket.getUsername();
    }

    @Tool(name = "getTicketStatus", description = "Fetch the status of the tickets based on a given username")
    public List<HelpDeskTicket> getTicketStatus(ToolContext toolContext) {
        String username = (String) toolContext.getContext().get("username");
        logger.info("Fetching tickets for user: {}", username);
        List<HelpDeskTicket> tickets = helpDeskTicketService.getTicketsByUsername(username);
        logger.info("Found {} tickets for user: {}", tickets.size(), username);
//        throw new RuntimeException("Unable to fetch ticket status");
        return tickets;
    }
}
