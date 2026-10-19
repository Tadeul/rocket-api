package br.com.fiap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API ROCKET (SoulUp).
 * Os pacotes bean, dao, bo, resource, exception e dto ficam abaixo de br.com.fiap
 * e sao encontrados automaticamente pelo component scan.
 */
@SpringBootApplication
public class RocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketApplication.class, args);
    }
}
