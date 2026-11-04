package com.imagina.demo.repository;

import com.imagina.demo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByActivoTrue();

    Optional<Cliente> findByIdAndActivoTrue(Long id);
}
