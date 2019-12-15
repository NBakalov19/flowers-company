package org.nbakalov.flowerscompany.services.services.implementations;

import com.cloudinary.Cloudinary;
import lombok.AllArgsConstructor;
import org.nbakalov.flowerscompany.services.services.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

  private final Cloudinary cloudinary;

  @Override
  public String uploadImage(MultipartFile multipartFile) throws IOException {

    File file = File.createTempFile("temp-file", multipartFile.getOriginalFilename());

    multipartFile.transferTo(file);

    return this.cloudinary.uploader()
            .upload(file, new HashMap())
            .get("url")
            .toString();
  }
}
