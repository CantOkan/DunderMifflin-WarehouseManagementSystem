package com.canok.whmanagement.controller;


import com.canok.whmanagement.dto.ClientDto;
import com.canok.whmanagement.entity.Client;
import com.canok.whmanagement.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<?> createClient(@RequestBody ClientDto clientDto){
        Boolean result=clientService.createClient(clientDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/find/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','EMPLOYEE')")
    public ResponseEntity<?> findClientById(@PathVariable Long id){
        Client client=clientService.findClientById(id);
        return ResponseEntity.ok(client);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','EMPLOYEE')")
    public ResponseEntity<?> updateClient(@PathVariable Long id, @RequestBody ClientDto clientDto){
        Client result = clientService.updateClient(id, clientDto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public ResponseEntity<?> deleteClient(@PathVariable Long id){
        Boolean result = clientService.deleteClient(id);
        return ResponseEntity.ok(result);
    }
}
