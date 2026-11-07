package com.rafael.notes_projeto.files.service;

import com.rafael.notes_projeto.configuration.S3Properties;
import com.rafael.notes_projeto.files.dto.FilesRequest;
import com.rafael.notes_projeto.files.dto.FilesResponse;
import com.rafael.notes_projeto.files.exception.FilesNotFoundException;
import com.rafael.notes_projeto.files.model.Files;
import com.rafael.notes_projeto.files.repository.FilesRepository;
import com.rafael.notes_projeto.pages.dto.PagesResponse;
import com.rafael.notes_projeto.pages.model.Pages;
import com.rafael.notes_projeto.pages.service.PagesService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FilesService {

  private final FilesRepository filesRepository;
  private final PagesService pageService;
  private final S3Client s3Client;
  private final S3Properties s3Properties;

  @Transactional(readOnly = true)
  public List<FilesResponse> findAll() {
    return filesRepository.findAll().stream().map(FilesResponse::from).toList();
  }

  @Transactional(readOnly = true)
  public ResponseInputStream<GetObjectResponse> findById(Long id) {
    Files file = buscar(id);
    System.out.println("FILE: " + file.getFilename());
    ResponseInputStream<GetObjectResponse> s3File = s3Client
        .getObject(GetObjectRequest.builder().bucket(s3Properties.bucket()).key(file.getFilename()).build());

    System.out.println("S3FILE: " + s3File);
    return s3File;
  }

  @Transactional
  public FilesResponse create(MultipartFile file, String page_id) {
    try {
      if (file.isEmpty()) {
        throw new RuntimeException("Failed to store empty file.");
      }

      PutObjectResponse s3File = s3Client
          .putObject(PutObjectRequest.builder().bucket(s3Properties.bucket()).key(file.getOriginalFilename())
              .contentType(file.getContentType()).build(), RequestBody.fromBytes(file.getBytes()));

      PagesResponse page = pageService.findById(Long.valueOf(page_id));
      Pages newPage = new Pages();
      newPage.setId(page.id());
      Files newFile = Files.builder().tag(s3File.eTag()).page(newPage).filename(file.getOriginalFilename()).build();
      return FilesResponse.from(filesRepository.save(newFile));

    } catch (IOException e) {
      throw new RuntimeException("Failed to store file.", e);
    }
  }

  @Transactional
  public FilesResponse update(Long id, FilesRequest request) {
    Files files = buscar(id);
    request.applyTo(files);
    return FilesResponse.from(filesRepository.save(files));
  }

  @Transactional
  public void delete(Long id) {
    filesRepository.delete(buscar(id));
  }

  private Files buscar(Long id) {
    return filesRepository.findById(id)
        .orElseThrow(() -> new FilesNotFoundException(id));
  }
}
