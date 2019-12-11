package org.nbakalov.flowerscompany.web.controllers;

import lombok.*;
import org.modelmapper.ModelMapper;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
public class BaseApiController {

  protected ModelMapper modelMapper;
}
