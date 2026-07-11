package com.velazco.velazco_back.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre", length = 100, nullable = false, unique = true)
  private String name; 

  @Column(name = "descripcion", length = 255)
  private String description;

  @OneToMany(mappedBy = "category")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Product> products;
}
