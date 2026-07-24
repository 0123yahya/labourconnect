package com.labourconnect.controller;

import com.labourconnect.entity.Client;
import com.labourconnect.service.ClientService;
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
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client saved = clientService.createClient(client);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Client> listClients() {
        return clientService.listClients();
    }

}