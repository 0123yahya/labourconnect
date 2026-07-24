package com.labourconnect.controller;

import com.labourconnect.dto.ClientRequest;
import com.labourconnect.entity.Client;
import com.labourconnect.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody ClientRequest request) {
        Client saved = clientService.createClient(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Client> listClients() {
        return clientService.listClients();
    }

}