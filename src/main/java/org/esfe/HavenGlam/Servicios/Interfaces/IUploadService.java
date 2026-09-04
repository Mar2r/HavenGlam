package org.esfe.HavenGlam.Servicios.Interfaces;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadService {
    String uploadFile(MultipartFile file) throws IOException;
}
