package org.nbakalov.flowerscompany.data.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.util.Set;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
public class Warehouse extends BaseEntity {

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "temperature", nullable = false)
  @DecimalMin(value = "-10.00")
  @DecimalMax(value = "5.00")
  private Double temperature;

  @Column(name = "current_capacity", nullable = false)
  @Min(0)
  private Integer currCapacity;

  @Column(name = "max_capacity", nullable = false)
  @Min(2500)
  private Integer maxCapacity;

  @OneToMany(mappedBy = "warehouse", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<FlowersBatch> batches;

}
