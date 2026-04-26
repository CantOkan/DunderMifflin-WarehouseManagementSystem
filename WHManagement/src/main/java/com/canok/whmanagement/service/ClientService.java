package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.ClientDto;
import com.canok.whmanagement.entity.Address;
import com.canok.whmanagement.entity.Client;
import com.canok.whmanagement.repository.AddressRepository;
import com.canok.whmanagement.repository.ClientRepository;
import com.canok.whmanagement.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    private final AddressRepository addressRepository;

    private final AddressService addressService;
    private final AuthorizationService authorizationService;
    private final PasswordEncoder passwordEncoder;

    public Boolean createClient(ClientDto clientDto) {
        return registerClient(clientDto) != null;
    }

    public Client registerClient(ClientDto clientDto) {
        if (clientDto.getUsername() == null || clientDto.getPassword() == null) {
            return null;
        }
        if (clientDto.getAddressDto() == null) {
            return null;
        }
        if (clientRepository.existsByUsername(clientDto.getUsername())) {
            return null;
        }
        if (clientDto.getAddressDto().getId() != null) {
            Optional<Address> optionalAddress = addressRepository.findById(clientDto.getAddressDto().getId());
            if (optionalAddress.isPresent()) {
                Client client = clientDto.convertToClient();
                client.setPassword(passwordEncoder.encode(clientDto.getPassword()));
                return clientRepository.save(client);
            }

        } else {
            Address address = addressService.createAddress(clientDto.getAddressDto());
            Client client = clientDto.convertToClient();
            client.setAddress(address);
            client.setPassword(passwordEncoder.encode(clientDto.getPassword()));
            return clientRepository.save(client);
        }
        return null;
    }

    public Client findClientById(Long id) {
        authorizationService.verifyClientOwnershipOrEmployee(id);
        return clientRepository.findById(id).orElse(null);
    }

    public Client updateClient(Long id, ClientDto clientDto) {
        authorizationService.verifyClientOwnershipOrEmployee(id);
        Optional<Client> optional = clientRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        Client client = optional.get();
        client.setName(clientDto.getName());
        if (clientDto.getUsername() != null) {
            if (!client.getUsername().equals(clientDto.getUsername()) &&
                    clientRepository.existsByUsername(clientDto.getUsername())) {
                return null;
            }
            client.setUsername(clientDto.getUsername());
        }
        if (clientDto.getPassword() != null) {
            client.setPassword(passwordEncoder.encode(clientDto.getPassword()));
        }
        if (clientDto.getAddressDto() != null) {
            Address address = addressService.createAddress(clientDto.getAddressDto());
            client.setAddress(address);
        }
        return clientRepository.save(client);
    }

    public Boolean deleteClient(Long id) {
        authorizationService.verifyClientOwnershipOrEmployee(id);
        if (!clientRepository.existsById(id)) {
            return false;
        }
        clientRepository.deleteById(id);
        return true;
    }
}
