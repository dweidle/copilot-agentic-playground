package de.weidle.copilotagenticplayground.greeting;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GreetingLogRepository extends JpaRepository<GreetingLog, Long> {}
