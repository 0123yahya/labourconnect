package com.labourconnect.controller;

import com.labourconnect.entity.Client;
import com.labourconnect.repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client saved = clientRepository.save(client);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Client> listClients() {
        return clientRepository.findAll();
    }

}