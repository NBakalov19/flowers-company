package org.nbakalov.flowerscompany.services.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {

  String uploadImage(MultipartFile multipartFile) throws IOException;
}
