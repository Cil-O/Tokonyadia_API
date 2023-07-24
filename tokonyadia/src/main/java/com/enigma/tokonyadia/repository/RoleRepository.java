package com.enigma.tokonyadia.repository;

import com.enigma.tokonyadia.entity.Role;
import com.enigma.tokonyadia.entity.constant.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByRole(ERole role);
}
