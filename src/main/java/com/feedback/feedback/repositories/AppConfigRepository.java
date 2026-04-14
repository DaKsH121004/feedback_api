package com.feedback.feedback.repositories;

import com.feedback.feedback.entities.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {
}
