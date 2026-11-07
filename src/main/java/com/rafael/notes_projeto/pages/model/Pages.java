package com.rafael.notes_projeto.pages.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rafael.notes_projeto.files.model.Files;
import com.rafael.notes_projeto.tags.model.Tags;

@Entity
@Table(name = "pages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pages {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String searchText;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private TiptapNode content;

  @ManyToMany
  @JoinTable(name = "page_tags", joinColumns = @JoinColumn(name = "page_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tags> tags;

  @ManyToMany()
  @JoinTable(name = "page_links", joinColumns = @JoinColumn(name = "source_page_id"), inverseJoinColumns = @JoinColumn(name = "target_page_id"))
  @JsonIgnore
  @Builder.Default
  private Set<Pages> links = new HashSet<>();

  @OneToMany(mappedBy = "page")
  @Builder.Default
  private Set<Files> files = new HashSet<>();

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
