package org.nbakalov.flowerscompany.data.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Set;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
public class Warehouse extends BaseEntity {

  @Column(name = "name", nullable = false, unique = true)
  @Size(min = 4, max = 20)
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
  @Max(50000)
  private Integer maxCapacity;

  @OneToMany(mappedBy = "warehouse", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private Set<FlowersBatch> batches;

}
