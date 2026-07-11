package com.velazco.velazco_back.model;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre", length = 100, nullable = false, unique = true)
  private String name;

  @Column(name = "precio", precision = 10, scale = 2, nullable = false)
  private BigDecimal price;

  @Column(name = "stock", nullable = false)
  private Integer stock;

  @Column(name = "imagen", length = 255)
  private String image;

  @Column(name = "activo", nullable = false)
  @ColumnDefault("true")
  private Boolean active;

  @ManyToOne
  @JoinColumn(name = "categoria_id", nullable = false)
  private Category category;

  @OneToMany(mappedBy = "product")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<OrderDetail> orderDetails;

  @OneToMany(mappedBy = "product")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<ProductionDetail> productionDetails;

}
