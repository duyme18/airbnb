package com.hoangducduy.airbnb.repository;

import com.hoangducduy.airbnb.constant.ERole;
import com.hoangducduy.airbnb.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
