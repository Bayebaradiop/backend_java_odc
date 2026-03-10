package com.medibook.cabinet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medibook.cabinet.entity.Cabinet;

@Repository
public interface CabinetRepository extends JpaRepository<Cabinet, Long> {

    List<Cabinet> findByStatus(Cabinet.Status status);
}
