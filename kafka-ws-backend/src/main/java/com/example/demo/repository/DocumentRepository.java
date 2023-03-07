package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.demo.model.DocItem;

//Init MongoDB with extended functionality.
public interface DocumentRepository extends MongoRepository<DocItem, String> {
    
}
