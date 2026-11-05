package com.rafael.notes_projeto.pages.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TiptapNode(
    String type,
    Map<String, Object> attrs,
    List<TiptapNode> content,
    List<TiptapMark> marks,
    String text) {

  public String plainText() {
    if (text != null)
      return text;
    if ("hardBreak".equals(type))
      return "\n";
    if (content == null || content.isEmpty())
      return "";

    Boolean inline = content().stream().anyMatch(c -> c.text() != null);
    return content().stream().map(TiptapNode::plainText).filter(s -> inline || !s.isBlank())
        .collect(Collectors.joining(inline ? "" : "\n"));

  }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
record TiptapMark(
    String type,
    Map<String, Object> attrs) {
}
