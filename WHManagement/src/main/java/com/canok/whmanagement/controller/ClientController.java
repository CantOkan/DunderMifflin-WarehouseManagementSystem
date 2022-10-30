package com.canok.whmanagement.controller;


import com.canok.whmanagement.dto.ClientDto;
import com.canok.whmanagement.entity.Client;
import com.canok.whmanagement.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class ClientController {


    @Autowired
    ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<?> createClient(@RequestBody ClientDto clientDto){
        Boolean result=clientService.createClient(clientDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findClientById(@PathVariable Long id){
        Client client=clientService.findClientById(id);
        return ResponseEntity.ok(client);
    }
}
