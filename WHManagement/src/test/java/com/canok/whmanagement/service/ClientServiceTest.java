package com.canok.whmanagement.service;

import com.canok.whmanagement.dto.AddressDto;
import com.canok.whmanagement.dto.ClientDto;
import com.canok.whmanagement.entity.Address;
import com.canok.whmanagement.entity.Client;
import com.canok.whmanagement.repository.AddressRepository;
import com.canok.whmanagement.repository.ClientRepository;
import com.canok.whmanagement.security.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock private ClientRepository clientRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private AddressService addressService;
    @Mock private AuthorizationService authorizationService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientService clientService;

    private AddressDto addressDto;
    private ClientDto clientDto;

    @BeforeEach
    void setUp() {
        addressDto = new AddressDto(null, "Paris", "FR", "Rue", 75001, 48.85, 2.35);
        clientDto = ClientDto.builder()
                .name("Acme").username("acme").password("pwd")
                .addressDto(addressDto).build();
    }

    @Test
    void registerClient_shouldReturnNull_whenCredentialsMissing() {
        ClientDto invalid = ClientDto.builder().username(null).password("p")
                .addressDto(addressDto).build();
        assertThat(clientService.registerClient(invalid)).isNull();
    }

    @Test
    void registerClient_shouldReturnNull_whenAddressMissing() {
        ClientDto invalid = ClientDto.builder().username("u").password("p").addressDto(null).build();
        assertThat(clientService.registerClient(invalid)).isNull();
    }

    @Test
    void registerClient_shouldReturnNull_whenUsernameTaken() {
        when(clientRepository.existsByUsername("acme")).thenReturn(true);
        assertThat(clientService.registerClient(clientDto)).isNull();
        verify(clientRepository, never()).save(any());
    }

    @Test
    void registerClient_shouldCreateAddressAndSave_whenAddressIdNull() {
        Address savedAddress = Address.builder().id(99L).city("Paris").build();
        when(clientRepository.existsByUsername("acme")).thenReturn(false);
        when(addressService.createAddress(addressDto)).thenReturn(savedAddress);
        when(passwordEncoder.encode("pwd")).thenReturn("ENC");
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client result = clientService.registerClient(clientDto);

        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isSameAs(savedAddress);
        assertThat(result.getPassword()).isEqualTo("ENC");
    }

    @Test
    void registerClient_shouldReuseAddress_whenAddressIdProvidedAndExists() {
        addressDto.setId(10L);
        Address existing = Address.builder().id(10L).build();
        when(clientRepository.existsByUsername("acme")).thenReturn(false);
        when(addressRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("pwd")).thenReturn("ENC");
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client result = clientService.registerClient(clientDto);

        assertThat(result).isNotNull();
        verify(addressService, never()).createAddress(any());
    }

    @Test
    void registerClient_shouldReturnNull_whenAddressIdProvidedButMissing() {
        addressDto.setId(10L);
        when(clientRepository.existsByUsername("acme")).thenReturn(false);
        when(addressRepository.findById(10L)).thenReturn(Optional.empty());

        assertThat(clientService.registerClient(clientDto)).isNull();
        verify(clientRepository, never()).save(any());
    }

    @Test
    void createClient_shouldDelegateToRegister() {
        when(clientRepository.existsByUsername("acme")).thenReturn(true);
        assertThat(clientService.createClient(clientDto)).isFalse();
    }

    @Test
    void findClientById_shouldVerifyAuthorizationAndReturnClient() {
        Client c = Client.builder().id(1L).build();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(c));

        assertThat(clientService.findClientById(1L)).isSameAs(c);
        verify(authorizationService).verifyClientOwnershipOrEmployee(1L);
    }

    @Test
    void findClientById_shouldReturnNull_whenMissing() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThat(clientService.findClientById(1L)).isNull();
    }

    @Test
    void updateClient_shouldReturnNull_whenNotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThat(clientService.updateClient(1L, clientDto)).isNull();
    }

    @Test
    void updateClient_shouldRejectDuplicateUsername() {
        Client existing = Client.builder().id(1L).username("old").build();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.existsByUsername("acme")).thenReturn(true);

        assertThat(clientService.updateClient(1L, clientDto)).isNull();
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClient_shouldApplyChanges() {
        Client existing = Client.builder().id(1L).username("acme").password("old").build();
        Address newAddress = Address.builder().id(2L).build();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(addressService.createAddress(addressDto)).thenReturn(newAddress);
        when(passwordEncoder.encode("pwd")).thenReturn("ENC");
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client result = clientService.updateClient(1L, clientDto);

        assertThat(result.getName()).isEqualTo("Acme");
        assertThat(result.getPassword()).isEqualTo("ENC");
        assertThat(result.getAddress()).isSameAs(newAddress);
    }

    @Test
    void deleteClient_shouldReturnFalse_whenMissing() {
        when(clientRepository.existsById(1L)).thenReturn(false);
        assertThat(clientService.deleteClient(1L)).isFalse();
        verify(clientRepository, never()).deleteById(any());
    }

    @Test
    void deleteClient_shouldDelete_whenExists() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        assertThat(clientService.deleteClient(1L)).isTrue();
        verify(clientRepository).deleteById(1L);
    }
}