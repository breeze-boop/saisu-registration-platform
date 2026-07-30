package com.mishi.controller;

import com.mishi.agent.AgentQuestion;
import com.mishi.agent.AgentReply;
import com.mishi.agent.CustomerAgentService;
import com.mishi.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
public class CustomerAgentController {
    private final CustomerAgentService customerAgentService;
    public CustomerAgentController(CustomerAgentService customerAgentService) { this.customerAgentService = customerAgentService; }
    @PostMapping("/chat") public ApiResponse<AgentReply> chat(@Valid @RequestBody AgentQuestion question) { return ApiResponse.ok(customerAgentService.chat(question)); }
}
