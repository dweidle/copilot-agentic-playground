package de.weidle.copilotagenticplayground.greeting.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GreetingLogJpaRepository extends JpaRepository<GreetingLogEntity, Long> {}
