package org.nbakalov.flowerscompany.data.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@Getter
@Setter
@NoArgsConstructor
public class Log extends BaseEntity {

  @Column(name = "created_on", nullable = false, updatable = false)
  private LocalDateTime createdOn;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "event", nullable = false, columnDefinition = "text")
  private String description;
}
