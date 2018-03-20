package edu.scu.qz.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    String upload(MultipartFile file, String path);

}
