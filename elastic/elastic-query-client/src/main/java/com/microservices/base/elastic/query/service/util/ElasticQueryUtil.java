package com.microservices.base.elastic.query.service.util;

import com.microservices.base.elastic.model.index.IndexModel;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ElasticQueryUtil<T extends IndexModel> {

    public Query getSearchQueryById(String id) {
        return new NativeSearchQueryBuilder() //Provides methods like withIds, withQuery to create a spring data elasticsearch Query Object
                .withIds(Collections.singleton(id))
                .build();
    }

    /*
    "query": {
        "bool": {
            "must": [
                {
                    "match": {
                        "text": "test"
                    }
                },
                {
                    "match": {
                        "text": "word"
                    }
                }
            ]
        }
    }
     */
    public Query getSearchQueryByFieldText(String field, String text) {
        return new NativeSearchQueryBuilder()
                //BoolQueryBuilder and QueryBuilders are the classes in elastic core library to create queries like match, term, matchAll etc
                .withQuery(new BoolQueryBuilder() //This is writing bool complex query with must that was explained below.
                        .must(QueryBuilders.matchQuery(field, text)))
                .build();
    }

    public Query getSearchQueryForAll() {
        return new NativeSearchQueryBuilder()
                .withQuery(new BoolQueryBuilder()
                        .must(QueryBuilders.matchAllQuery()))
                .build();
    }
}

//http://localhost:9200/twitter-index/_doc/1 -> the _doc is the default and only mapping allowed in the latest releases.
//Restricted to single mapping per index in latest releases, no possibility of adding more than one mapping to a single index.
//And hence, it uses a generic name _doc for that.
//And, it is not mandatory to specify it
//at the end of the path we pass 1, as the ID of the document
/*
{
    "query": {
        "term": {
            "text": "test"
        }
    }
}
This will return all the results that include "test" in the text field.
 */

/*
{
    "query": {
        "match": {
            "text": "test multi word"
        }
    }
}
This will return all the results that have either test or multi or word and combine the results and send back to us.
It actually separates the three words by spaces and match test, multi and word against elastic search.
 */

/*
{
    "query": {
        "term": {
            "text": "test multi word"
        }
    }
}
This will not give back any results, amazed right?
The term uses exact term and doesn't analyse the input. So, for a multi word sentence if you search the sentence with all terms in it
it will look for the whole sentence in inverted index.
Also, during indexing that we will do via the elastic-index-client(two impl -> ElasticSearchOperation and ElasticSearchRepository), analyzers comes into place and use inverted index to index the provided words separately and not the whole word. We did the index when saving to elasticsearch using POST req -> http://localhost:9200/twitter-index/_doc/1
 */

/*
Then, how do we search the whole sentence using the "term"?
Using keyword
remember the mapping we did
"text": {
                "type": "text",
                "fields": {
                    "keyword": {
                        "type": "keyword",
                        "ignore_above": 256
                    }
                }
            }
Now use the JSON as text.keyword and you will get the result, because the keyword type is saved as whole text instead of analyzing it.
With .keyword all query types run without analysing the search text.
This query will also work for match.
However, better to use term query with keyword like queries where we search for the exact term, because term query is bit fast as compared to match as there is no analyse step.
{
    "query": {
        "term": {
            "text.keyword": "test multi word"
        }
    }
}
 */

/*
Wildcard query : uses wildcard to match the search term, can be slow as it requires scan.
{
    "query": {
        "wildcard": {
            "text": "te*"
        }
    }
}
Returns, all documents that include the word that start with te. Here * means 0 or more, we can also use ? for single character.
 */

/*
Query String -> we can use wildcard here as well, but analyses the input. Can give multiple fields in query_string fields property
to search inside all fields. Similar to wildcard but more flexible.
{
    "query": {
        "query_string": {
            "query": "text:te*, test*, wo*, mu*, word"
        }
    }
}
 */

/*
We can also write complex queries like below :
{
    "from": 0,
    "size": 20,
    "query": {
        "bool": {
            "must": [
                {
                    "match": {
                        "text": "test"
                    }
                },
                {
                    "match": {
                        "text": "word"
                    }
                }
            ]
        }
    }
}
Above query will return all documents that include test and word together. Must go with and operation
If we change must with should, it will return all documents that include either test or word. Should go with OR operation.
 */