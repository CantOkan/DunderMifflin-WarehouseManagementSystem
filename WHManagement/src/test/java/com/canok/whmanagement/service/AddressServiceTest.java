package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.AddressDto;
import com.canok.whmanagement.entity.Address;
import com.canok.whmanagement.repository.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @Test
    void createAddress_shouldPersistConvertedAddress() {
        AddressDto dto = new AddressDto(null, "Berlin", "DE", "Main", 10115, 52.5, 13.4);
        Address saved = Address.builder().id(1L).city("Berlin").country("DE").street("Main")
                .postCode(10115).latitude(52.5).longitude(13.4).build();
        when(addressRepository.save(any(Address.class))).thenReturn(saved);

        Address result = addressService.createAddress(dto);

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).save(captor.capture());
        assertThat(captor.getValue().getCity()).isEqualTo("Berlin");
        assertThat(captor.getValue().getCountry()).isEqualTo("DE");
        assertThat(result.getId()).isEqualTo(1L);
    }
}