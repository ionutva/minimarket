package com.example.marketorder.repository;

import com.example.marketorder.model.Execution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionRepository extends JpaRepository<Execution, Long> {
}
