package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.Address;
import com.enigma.tokonyadia.repository.AddressRepository;
import com.enigma.tokonyadia.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public Address create(Address address) {
        return addressRepository.save(address);
    }
}
