package org.nbakalov.flowerscompany.data.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@Getter
@Setter
public class Order extends BaseEntity {

  @ManyToOne(targetEntity = User.class)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User customer;

  @Column(name = "variety")
  @Enumerated(value = EnumType.STRING)
  private Variety variety;

  @Column(name = "quantity", nullable = false)
  @Min(1)
  private Integer quantity;

  @Column(name = "bunches_per_tray", nullable = false)
  @Min(10)
  @Max(25)
  private Integer bunchesPerTray;

  @Column(name = "order_date_time", nullable = false, updatable = false)
  private LocalDateTime orderDateTime;

  @Column(name = "finished_on")
  private LocalDateTime finishedOn;

  @Column(name = "status", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private Status status;
}
