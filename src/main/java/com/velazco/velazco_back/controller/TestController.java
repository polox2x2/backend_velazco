package com.velazco.velazco_back.controller;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@Tag(name = "Pruebas", description = "Endpoints de prueba del sistema")
@RestController
@RequiredArgsConstructor
public class TestController {

  private final Clock clock;

  @GetMapping("/")
  public String hello() {
    return "Hello world from Java Spring Boot!";
  }

  @GetMapping("/clock")
  public String getDateNow() {
    LocalDate today = LocalDate.now(clock);
    return "Hoy en Perú: " + today;
  }
}
