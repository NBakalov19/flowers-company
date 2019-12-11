package org.nbakalov.flowerscompany.data.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum Variety {
  APOTHEOSE("Apotheose"),
  BUTTERFLY("Butterfly"),
  CARLTON("Carlton"),
  DICKWILDEN("Dick Wilden"),
  ERLICHEER("Erlicheer"),
  FLOWERPARADE("Flower Parade"),
  GOLDENDUCAT("Golden Ducat"),
  HOLLANDSENSATION("Holland Sensation"),
  ICEKING("Ice King"),
  JERSEYHARVEST("Jersey Harvest"),
  KINGALFRED("King Alfred"),
  LEMONBEAUTY("Lemon Beauty"),
  MANDO("Mando"),
  NEWBABY("New Baby"),
  OBDAM("Obdam"),
  PINKPRIDE("Pink Pride"),
  QUALI("Quali"),
  REDDEVON("Red Devon"),
  STPATRICKDAY("St. Patrick Day"),
  TAHITI("Tahiti"),
  UNSURPASSBLE("Unsurpassble"),
  VANILLAPEACH("Vanilla Peach"),
  WHITELION("White Lion");

  private final String varietyName;

  public static Stream<Variety> stream() {
    return Stream.of(Variety.values());
  }
}
