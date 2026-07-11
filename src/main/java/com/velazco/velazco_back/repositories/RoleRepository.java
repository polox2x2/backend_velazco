package com.velazco.velazco_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_back.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}