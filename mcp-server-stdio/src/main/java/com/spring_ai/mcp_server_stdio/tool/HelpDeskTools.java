package com.spring_ai.mcp_server_stdio.tool;

import com.spring_ai.mcp_server_stdio.entity.HelpDeskTicket;
import com.spring_ai.mcp_server_stdio.model.TicketRequest;
import com.spring_ai.mcp_server_stdio.service.HelpDeskTicketService;
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
        HelpDeskTicket savedTicket = helpDeskTicketService.createTicket(ticketRequest);
        logger.info("Ticket created successfully, Ticket ID: {}, Username {}", savedTicket.getId(), savedTicket.getUsername());
        return "Ticket #" + savedTicket.getId() + " created successfully for user " + savedTicket.getUsername();
    }

    @Tool(name = "getTicketStatus", description = "Fetch the status of the tickets based on a given username")
    public List<HelpDeskTicket> getTicketStatus(@ToolParam(description =
            "Username to fetch the status of the help desk tickets") String username) {
        logger.info("Fetching tickets for user: {}", username);
        List<HelpDeskTicket> tickets = helpDeskTicketService.getTicketsByUsername(username);
        logger.info("Found {} tickets for user: {}", tickets.size(), username);
        return tickets;
    }
}