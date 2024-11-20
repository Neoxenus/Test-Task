package com.my;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for store data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */

public class DocumentManager {


    private final List<Document> storage;

    public DocumentManager() {
        this.storage = new ArrayList<>();
    }

    /*
        Simple function for generating id
        By default just insert id with storage.size().
        If we try to insert document with custom id I put this while to omit collisions.
        Why complicate things on example like this :)
     */
    private String generateID(){
        String id = String.valueOf(storage.size());
        while(this.findById(id).isPresent()){
            id += "s";
        }
        return id;
    }
    private boolean check(Document document, SearchRequest searchRequest){

//        some prefixes must be included
        if(searchRequest.getTitlePrefixes() != null && !searchRequest.getTitlePrefixes().isEmpty()){
            boolean titlePrefixIsFound = false;
            for (String titlePrefix : searchRequest.getTitlePrefixes())
                if (document.getTitle().startsWith(titlePrefix)){
                    titlePrefixIsFound = true;
                    break;
                }
            if(!titlePrefixIsFound)
                return false;
        }
//        all content must be included
        if(searchRequest.getContainsContents() != null && !searchRequest.getContainsContents().isEmpty()){
            for (String content : searchRequest.getContainsContents())
                if (!document.getContent().contains(content)) {
                    return false;
                }
        }
//        some authors must be included
        if(searchRequest.getAuthorIds() != null && !searchRequest.getAuthorIds().isEmpty()){
            boolean authorIdIsFound = false;
            for (String authorId : searchRequest.getAuthorIds())
                if (document.getAuthor().getId().equals(authorId)){
                    authorIdIsFound = true;
                    break;
                }
            if(!authorIdIsFound)
                return false;
        }
        if(searchRequest.getCreatedFrom() != null ){
            if(document.getCreated().isBefore(searchRequest.getCreatedFrom()))
                return false;
        }
        if(searchRequest.getCreatedTo() != null){
            if(document.getCreated().isAfter(searchRequest.getCreatedTo()))
                return false;
        }
        return true;
    }

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        /*
            Check if id is exists. Considered that id is empty when its null. If its empty it will process as usual.
         */
        if(document.getId() == null){
            String newID = generateID();
            document.setId(newID);
        }
        /*
            Check if document with such id is already stored.
                Not inserting/saving it if  document with such id is already exists.
            Could omit redundant check after previous if-statement bcs we already created unique id.
                But whatever if its shorter code and we don`t care about performance.
         */
        if(findById(document.getId()).isEmpty()){
            storage.add(document);
            return document;
        }
        return null;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        /*
            I assumed:
                - if field is null we dont care about it at all.
                - if we match at least ONE title prefix we got check on titlePrefixes
                - same for author Ids
                - if we match all containsContents from searchRequest document suit us
         */
        if(storage.isEmpty()){
            return Collections.emptyList();
        }

        if(request == null){
           /*
            Idk what to do here. I will just return all storage (:
         */
            return storage;
        }
        List<Document> result = new ArrayList<>();
        for (Document document : storage) {
            if(check(document, request)){
                result.add(document);
            }
        }
        return result;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        /*
            Just simple run through list
         */
        for (Document document : storage) {
            if(document.getId().equals(id))
                return Optional.of(document);
        }
        return Optional.empty();
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}