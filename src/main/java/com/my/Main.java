package com.my;

import java.time.Instant;
import java.util.List;

public class Main {
    /*
        Here I run few tests for myself.
     */
    public static void main(String[] args) {
        DocumentManager documentManager = new DocumentManager();


        documentManager.findById(null);
        DocumentManager.Document document = new DocumentManager.Document.DocumentBuilder()
                .title("title")
                .content("content")
                .created(Instant.now())
                .author(DocumentManager.Author.builder()
                        .id("1")
                        .name("author1")
                        .build())
                .build();
        System.out.println(documentManager.save(document));

        System.out.println("content");
        System.out.println(documentManager.search(DocumentManager.SearchRequest.builder().containsContents(List.of("content")).build()));
        System.out.println("contentNegativeTest");
        System.out.println(documentManager.search(DocumentManager.SearchRequest.builder().containsContents(List.of("a")).build()));

        System.out.println("title");
        System.out.println(documentManager.search(DocumentManager.SearchRequest.builder().titlePrefixes(List.of("titl", "a")).build()));
        System.out.println("titleNegativeTest");
        System.out.println(documentManager.search(DocumentManager.SearchRequest.builder().titlePrefixes(List.of("itle")).build()));

        System.out.println("author");
        System.out.println(documentManager.search(DocumentManager.SearchRequest.builder().authorIds(List.of("1","0")).build()));
        System.out.println("authorNegativeTest");
        System.out.println(documentManager.search(DocumentManager.SearchRequest.builder().authorIds(List.of("0")).build()));

        System.out.println("createdTo");
        System.out.println(documentManager.search(DocumentManager.SearchRequest.builder().createdTo(Instant.now()).build()));
        System.out.println("createdFromNegativeTest");
        System.out.println(documentManager.search(DocumentManager.SearchRequest.builder().createdFrom(Instant.now()).build()));

        System.out.println("complexTest");
        System.out.println(documentManager.search(DocumentManager.SearchRequest.builder()
                .createdTo(Instant.now())
                .authorIds(List.of("1","0"))
                .build()));


        System.out.println("complexTestNegative");
        System.out.println(documentManager.search(DocumentManager.SearchRequest.builder()
                .createdTo(Instant.now())
                .authorIds(List.of("1","0"))
                .containsContents(List.of("content", "a"))
                .build()));


        System.out.println(documentManager.findById("0"));
    }
}