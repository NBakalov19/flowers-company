package org.nbakalov.flowerscompany.data.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

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
  private Integer quantity;

  @Column(name = "bunches_per_tray", nullable = false)
  private Integer bunchesPerTray;

  @Column(name = "order_date", nullable = false, updatable = false)
  private LocalDate orderDate;

  @Column(name = "finished_on")
  private LocalDate finishedOn;

  @Column(name = "status", nullable = false)
  @Enumerated(value = EnumType.STRING)
  private Status status;
}
