package com.healthai.repository;

import com.healthai.entity.PatientHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientHistoryRepository extends JpaRepository<PatientHistory, Long> {

    List<PatientHistory> findByUserIdOrderByAssessmentDateDesc(Long userId);

    Optional<PatientHistory> findByIdAndUserId(Long id, Long userId);
}
