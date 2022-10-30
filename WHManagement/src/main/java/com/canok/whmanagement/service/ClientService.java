package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.ClientDto;
import com.canok.whmanagement.entity.Address;
import com.canok.whmanagement.entity.Client;
import com.canok.whmanagement.repository.AddressRepository;
import com.canok.whmanagement.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressService addressService;

    public Boolean createClient(ClientDto clientDto){
        if(clientDto.getAddressDto().getId()!=null){
            Optional<Address> optionalAddress=addressRepository.findById(clientDto.getAddressDto().getId());
            if (optionalAddress.isPresent()){

                Client client=clientRepository.save(clientDto.convertToClient());
                if(client.getId()!=null)
                    return true;
            }

        }
        else{
            Address address=addressService.createAddress(clientDto.getAddressDto());
            Client client=clientDto.convertToClient();
            client.setAddress(address);
            Client result=clientRepository.save(client);
            if(result.getId()!=null)
                return true;
        }
        return false;
    }

    public Client findClientById(Long id){
        Optional<Client> optionalClient=clientRepository.findById(id);
        if(optionalClient.isPresent()){
            return optionalClient.get();
        }

        return null;
    }
}
