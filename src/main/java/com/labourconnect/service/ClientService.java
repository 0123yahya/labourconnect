package com.labourconnect.service;

import com.labourconnect.entity.Client;
import com.labourconnect.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public List<Client> listClients() {
        return clientRepository.findAll();
    }

    // Finds a client by phone number, or creates a new one if none exists -
    // this mirrors what the WhatsApp bot will do automatically in Stage 2.
    public Client findOrCreateByPhoneNumber(String phoneNumber, String name) {
        return clientRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setPhoneNumber(phoneNumber);
                    newClient.setName(name);
                    return clientRepository.save(newClient);
                });
    }
}