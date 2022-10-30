package com.canok.whmanagement.dto;


import com.canok.whmanagement.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDto {

    private Long id;
    private String city;
    private String country;
    private String street;
    private Integer postCode;

    public Address convertToAddres(){
        return Address.builder().id(id).city(city).country(country).street(street).postCode(postCode).build();
    }
}
