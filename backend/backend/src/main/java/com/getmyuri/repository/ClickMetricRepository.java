package com.getmyuri.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.getmyuri.model.ClickMetric;

@Repository
public interface ClickMetricRepository extends JpaRepository<ClickMetric, Long> {
}
