package org.nbakalov.flowerscompany.data.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Entity
@Table(name = "flowers_batches")
@NoArgsConstructor
@Getter
@Setter
public class FlowersBatch extends BaseEntity {

  @Column(name = "team_supervisior", nullable = false)
  private String teamSupervisor;

  @Column(name = "field_name", nullable = false)
  private String fieldName;

  @Enumerated(EnumType.STRING)
  @Column(name = "variety", nullable = false)
  private Variety variety;

  @Column(name = "trays", nullable = false)
  @Min(1)
  private Integer trays;

  @Column(name = "bunches_per_tray", nullable = false)
  @Min(10)
  @Max(25)
  private Integer bunchesPerTray;

  @Column(name = "date_picked", nullable = false)
  private LocalDate datePicked;

  @ManyToOne(targetEntity = Warehouse.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "warehouse_id", referencedColumnName = "id", nullable = false)
  private Warehouse warehouse;
}
