package org.nbakalov.flowerscompany.data.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "flowers_batches")
@NoArgsConstructor
@Getter
@Setter
public class FlowersBatch extends BaseEntity {

  @Column(name = "team_supervisior", nullable = false)
  @Size(min = 3, max = 20)
  private String teamSupervisor;

  @Column(name = "field_name", nullable = false)
  @Size(min = 3, max = 20)
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
  private LocalDateTime datePicked;

  @ManyToOne(targetEntity = Warehouse.class)
  @JoinColumn(name = "warehouse_id", referencedColumnName = "id", nullable = false)
  private Warehouse warehouse;
}
