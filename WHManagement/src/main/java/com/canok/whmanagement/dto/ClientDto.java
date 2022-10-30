package com.canok.whmanagement.dto;

import com.canok.whmanagement.entity.Address;
import com.canok.whmanagement.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {

    private String name;
    private AddressDto addressDto;

    public Client convertToClient(){
        return Client.builder().name(name).address(addressDto.convertToAddres()).build();
    }
}
