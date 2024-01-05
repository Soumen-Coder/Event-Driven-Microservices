package com.microservices.base.kafka.to.elastic.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.base")
public class KafkaToElasticServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaToElasticServiceApplication.class, args);
    }
}

//Elasticsearch Basics Introduced here
//E.S is an Open Source search Engine.
//Runs Apache Lucene Search Engine in the background which is  -> High performance, full-featured, text search engine.
//Organizes data using documents and makes it easily available and searchable
//Easy RestFul API based on JSON, can be queried using postman and uses JSON for communications.
//Easy Scale by adding more nodes or more shards for an index.
//Query DSL for complex queries
//Type Guessing, Dynamic Mapping which understands type of a field even though it is not explicitly defined.
//Lucene Standard Analysers which can be chosen according to needs.
//Concept of Inverted text -> allows to make fast full searches
//Inverted Index consists of a list of all the unique words that appear in any document, and for each word, a list of document in which it appears.
//You can find only the terms that exists in your index, so both indexed text and the query string must be normalized into the same form.

//How does E.S processes a text before indexing -> It uses built-in analysers
//Analysing consists of 3 steps
//1) Character filters: Tidy up the strings like strip out HTML contents, change & to and, etc.
//2) Tokenizers: Tokenize the string into individual words and split the strings using whitespace or punctuations.
//3) Token Filters: Filter out the string like change the word like "lowercase", remove stopwords such as a, the, and finally add words to synonyms.

//E.S provides many such Character Filters, tokenizers, and token filters out of the box. These can be combined to create custom analyzers suitable for different purposes.
//Basic DataTypes that can be used in E.S ->
//Binary : Base64 encoded string
//Numbers: Integer, long, double..
//Boolean: true/false
//Text: analysed, unstructured text
//Keywords: keywords, wildcards(Not analysed)
//Object: A JSON Object
//Date - a formatted date as string, An integer that represents seconds since epoch, A Long that that represents milliseconds since epoch.