package com.microservices.base.elastic.query.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor //used to construct java object while deserialize json
@AllArgsConstructor //required by Builder
//RepresentationModel is used to include Hypermedia controls to the responses of the spring web application, it will change the
//response model to a REST representational model so that hateoas related links can be set on it.
public class ElasticQueryServiceResponseModel extends RepresentationModel<ElasticQueryServiceResponseModel> {
    //Defining all fields present within elastic search
    private String id;
    private Long userId;
    private String text;
    private String textV2; // think if this is a breaking change? actually no, because of the extensible nature of json, by adding a new field, we are okay on the client side and deserialization will be done correctly, only it will not include the new field "textV2"
    //if any client need this field, then client can change their code, to include this field during deserialization process
    //This change doesn't require a new creation of a version
    //We will deploy the changes and mention them in the documentation
    //This is enough safe and easy for both server and client

    //Now let's say we want to change the type of id field to Long and also remove the createdAt field, then it will be breaking change and we have to create a new version
    private LocalDateTime createdAt;
}

//HATEOAS -> Hypermedia As The Engine Of Application State -> Important feature of the REST WebServices
//Richardson Maturity Model's top level is the Hypermedia Controls -> proof of Rest APIs completeness
//The Richardson Maturity Model -> Single URI & Single Verb < Resources with multiple URI & Single Verb < Multiple URI & HTTTP Verbs(GET(Safe), POST(Inserts), PUT(Update), DELETE(Delete), PATCH(incremental update) < Hypermedia Controls
//Hateoas says that representations for rest resources should contain links to related resources, not only the data itself but response from server should provide informations dynamically with hypermedia
//Helps clients to better understand APIs and use of APIs is easier
//Helps reduce risks of client breaking for api changes.

//APIs will evolve and Changes in API is inevitable, what kind of changes to be allowed and how do we do the changes,also how do we actually manage versioning?
//Good to have a formal versioning in place, can use semantic versioning, major.minor.patch inside the version definitions -> https://semver.org
//A major version for breaking changes
//Can also add a new minor version for non-breaking changes, including in docs may be enough.
//For non-breaking changes, updating the server and including minor version change in the documentation would be enough.
//This way client will only see a change in docs and doesn't need to update its client code or way of sending the data or parsing the response.
//Can only add patch version in the version definition in case of patching. But what about major changes?

//Consider forward and backward compatibility -> concepts to understand major breaking changes, when it will need and is it needed?
//Backward compatibility -> If you have an updated server and an updated client which is updated to work with the updated server then the new client will be able to work with the old server code.
//Forward compatibility -> old client will be able to work with an updated server without doing any change on the client side -> can be achieved by extensible schemas
//So considering the compatibilities, when do we need to create a new version?
//First, you need to create a new version if you want Backward Compatibility. Because it implies to have two versions in live by definition since the new client would want to work with both new and old server.
//Second, if you cannot be Forward Compatible which implies that you have a breaking change, then it will end up with a client that cannot work with the new server and it need to be updated, in that case you will need a new version as you must keep the old version live until client is ready for the new breaking change in the system.
//So, we can say that we can avoid breaking changes and let the clients work with the new server changes foe example, using extensible schemas which is easier to implement with JSON then we can avoid new versions.
//If you don't have a breaking change, don't update the major version but update the software and only include minor change in docs.
//so that version number in url or accept header will remain the same and nothing will change for client library or request data.
//Avoiding a major version update is more preferable -> less work for client and sever both in case of REST APIs

//Let's say if we couldn't escape the breaking changes, then how do we do versioning in our systems?
//We have 4 options
//1) URI Versioning -> version in the url
// - Leads to large URI footprint, difficult to maintain and use, less flexible. Against the hypermedia driven REST APIs, which states initial uri should not be changed and be the only info given to the client with the media types where rest will be resolved by the hypertext return to the client(hateoas).
// - Doesn't belong to contract
// - Easier to use by client as using other options require more programmatic approach
// - More Cache friendly as caching with uri is very easy. However, using a header for that purpose requires much work.
// Ex: http://localhost:8080/v1/documents

//2) MediaType versioning(Content-Negotiation) - representation using accept header and custom vendor type.
// - Difficulty with caching, more work to implement especially at the client side
// - It is part of the REST API contract.
// - Custom vendor types bring more semantic information.
// - Flexible to work with Rest APIs work well with hateoas and Level 3 rest api acc to Richardson maturing model.
// Ex: 'Accept: application/vnd.api.v1+json'

// 3) Custom Http Headers : version with custom header
// - Less standard and requires more alignment with client.
// - Similar to accept header and more flexible and works well with Level 3 APIs
//Ex: My-custom-header: v2

//4) Query Parameters: version with query parameters
// - Difficult to use with routing
// - Easy to use
//Ex : http://localhost/documents?version=1