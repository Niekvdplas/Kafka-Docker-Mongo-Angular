package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//DocItem as in Mongo DB
@Document("docs")
public class DocItem {

        @Id
        private String id;
        private FileModel content;
        
        public DocItem(String id, FileModel content) {
            super();
            this.id = id;
            this.content = content;
        }

        public FileModel getContent(){
            return this.content;
        }
}