package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.AddressDto;
import com.canok.whmanagement.entity.Address;
import com.canok.whmanagement.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public Address createAddress(AddressDto addressDto) {
        return addressRepository.save(addressDto.convertToAddres());
    }
}
