package com.imagina.demo.repository;

import com.imagina.demo.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Page<Cliente> findByActivoTrue(Pageable pageable);

    Optional<Cliente> findByIdAndActivoTrue(Long id);
}
