package com.kwsp.defendant;

import com.kwsp.defendant.model.PaymentRecord;
import com.kwsp.defendant.repository.PaymentRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackageClasses = PaymentRecord.class)
@EnableJpaRepositories(
        basePackageClasses = PaymentRepository.class
)
public class BackendSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                BackendSpringbootApplication.class,
                args
        );
    }
}