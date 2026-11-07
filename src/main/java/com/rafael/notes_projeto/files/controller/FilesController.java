package com.rafael.notes_projeto.files.controller;

import com.rafael.notes_projeto.files.dto.FilesRequest;
import com.rafael.notes_projeto.files.dto.FilesResponse;
import com.rafael.notes_projeto.files.service.FilesService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FilesController {

  private final FilesService filesService;

  @GetMapping
  public List<FilesResponse> findAll() {
    return filesService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<InputStreamResource> findById(@PathVariable Long id) {
    ResponseInputStream<GetObjectResponse> s3File = filesService.findById(id);
    GetObjectResponse meta = s3File.response();

    MediaType contentType = meta.contentType() != null
        ? MediaType.parseMediaType(meta.contentType())
        : MediaType.APPLICATION_OCTET_STREAM;

    return ResponseEntity.ok()
        .contentType(contentType)
        .contentLength(meta.contentLength())
        .body(new InputStreamResource(s3File));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FilesResponse create(@RequestParam("file") MultipartFile request, @RequestParam("page_id") String page_id) {
    return filesService.create(request, page_id);
  }

  @PutMapping("/{id}")
  public FilesResponse update(@PathVariable Long id, @RequestBody FilesRequest request) {
    return filesService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    filesService.delete(id);
  }

  // @PostMapping("/")
  // public String handleFileUpload(@RequestParam("file") MultipartFile file,
  // RedirectAttributes redirectAttributes) {
  //
  // filesService.create(file);
  //
  // redirectAttributes.addFlashAttribute("message",
  // "You successfully uploaded " + file.getOriginalFilename() + "!");
  //
  // return "redirect:/";
  // }
}
