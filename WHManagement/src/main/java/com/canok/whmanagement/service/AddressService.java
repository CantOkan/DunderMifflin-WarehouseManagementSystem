package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.AddressDto;
import com.canok.whmanagement.entity.Address;
import com.canok.whmanagement.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    AddressRepository addressRepository;

    public Address createAddress(AddressDto addressDto){
        Address address=addressRepository.save(addressDto.convertToAddres());
        return address;
    }
}
