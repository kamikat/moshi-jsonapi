# Publish

- update the version
- ```./gradlew clean assembleRelease```
- ```./gradlew artifactoryPublish```

# Change log

## Version 3.5.1

* Update to new Artifactory

## Version 3.5.0

* Moved the source repository of moshi-jsonapi to the SeatCode artifactory to avoid problems of availability

# moshi-jsonapi

[![Build Status](https://travis-ci.org/kamikat/moshi-jsonapi.svg?branch=master)](https://travis-ci.org/kamikat/moshi-jsonapi)
[![Coverage Status](https://coveralls.io/repos/github/kamikat/moshi-jsonapi/badge.svg?branch=master)](https://coveralls.io/github/kamikat/moshi-jsonapi?branch=master)
[![Download](https://api.bintray.com/packages/kamikat/maven/moshi-jsonapi/images/download.svg)](https://bintray.com/kamikat/maven/moshi-jsonapi/_latestVersion)

Java implementation of [JSON API](http://jsonapi.org/) specification v1.0 for [moshi](https://github.com/square/moshi).

## Getting Started

```java
JsonAdapter.Factory jsonApiAdapterFactory = ResourceAdapterFactory.builder()
        .add(Article.class)
        .add(Person.class)
        .add(Comment.class)
        // ...
        .build();
Moshi moshi = new Moshi.Builder()
        .add(jsonApiAdapterFactory)
        // ...
        .build();
```

You're now ready to serialize/deserialize JSON API objects with cool Moshi interface!

```java
String json = "...";
ArrayDocument<Article> articles = moshi.adapter(Document.class).fromJson(json).asArrayDocument();
for (Article article : articles) {
  System.out.println(article.title);
}
```

## Usage

### Resource Object

Extend a `Resource` class to create a model for resource object.

```java
@JsonApi(type = "people")
class Person extends Resource {
    @Json(name="first-name") String firstName;
    @Json(name="last-name") String lastName;
    String twitter;
}
```

`@JsonApi(type = ...)` annotation identifies each model by `type` as is mentioned in specification.

### Relationships

There are two kinds of relationship defined in JSON API specification.
Defining these relationship in resource object is quite simple:

```java
@JsonApi(type = "articles")
public class Article extends Resource {
    public String title;
    public HasOne<Person> author;
    public HasMany<Comment> comments;
}
```

Relationships can be resolved to resource object in a `Document`:

```java
Person author = article.author.get(article.getDocument());
```

You can use `Resource.getDocument()` to access the `Document` object the `Resource` be added/included in.
Further more, with a little bit encapsulation:

```java
@JsonApi(type = "articles")
public class Article extends Resource {
    private String title;
    private HasOne<Person> author;
    private HasMany<Comment> comments;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Person getAuthor() {
        return author.get(getDocument());
    }

    public List<Comment> getComments() {
        return comments.get(getDocument());
    }
}
```

### Document

`Document` interfaces denotes a JSON API document, document object contains one of the following attributes:

- `data` the primary data, can be null, resource object or array of resource object
- `error` error object
- `meta`

To keep consistency with the specification, moshi-jsonapi implements `ArrayDocument<T>` and `ObjectDocument<T>`.
`Document` object can be converted with `Document.<T>asXDocument()` function.

```java
ObjectDocument<Article> document = new ObjectDocument<>();
document.set(article);
document.addInclude(author);

// Serialize
System.out.println(moshi.adapter(Document.class).toJson(document));
// => {
//      data: { "type": "articles", "relationships": { "author": { "data": "type": "people", id: "1" } } },
//      included: [
//        { "type": "people", "attributes": { "first-name": "Yuki", "last-name": "Kiriyama", "twitter": "kamikat_bot" } }
//      ]
//    }

// Deserialize
Document document2 = adapter.fromJson(...);
ObjectDocument<Article> document3 = document2.asObjectDocument();
assert document3.get() instanceof Article
assert document3.get().getDocument() == document3
```

The linkage (relationship) of a resource object is resolved in document of the resource object (check `Resource.getDocument()`).

### Default Resource Type

Create a `default` typed class to have all unknown type parsed in the class to avoid deserialization error processing unknown type of resource.

```java
@JsonApi(type = "default")
class Unknown extends Resource {
    // nothing...
}
```

### meta/links/jsonapi Properties

You'd like to access `meta`/`links`/`jsonapi` value on `Document` for example.

```java
Document document = ...;
document.getMeta() // => JsonBuffer
```

As `meta` and `links` can contain a variant of objects, they are not been parsed when access with `getMeta` and `getLinks`.
You will get a `JsonBuffer` and you're expected to implement your `JsonAdapter<T>` to read/write these objects.

### Retrofit

Retrofit extension library (see following section) provides `JsonApiConverterFactory` to get integrate with Retrofit 2.
Here's an example:

```java
Retrofit retrofit = new Retrofit.Builder()
        // ...
        .addConverterFactory(JsonApiConverterFactory.create(moshi))
        .build()
retrofit.create(MyAPI.class);
```

And `MyAPI` interface:

```java
public interface MyAPI {

    @GET("posts")
    Call<Post[]> listPosts();

    @GET("posts/{id}")
    Call<Post> getPost(@Path("id") String id);

    @GET("posts/{id}/comments")
    Call<List<Comment>> getComments(@Path("id") String id);

    @POST("posts/{id}/comments")
    Call<Document> addComment(@Path("id") String id, @Body Comment comment);

    @GET("posts/{id}/relationships/comments")
    Call<ResourceIdentifier[]> getCommentRels(@Path("id") String id);
}
```

Note that the body can either be serialized/deserialized to resource object or document object with additional information.

## Download

In gradle build script:

```groovy
repositories {
    jcenter()
}

dependencies {
    implementation 'com.squareup.moshi:moshi:1.4.0'                        // required, peer dependency to moshi
    implementation 'moe.banana:moshi-jsonapi:<version>'                    // required, core library
    implementation 'moe.banana:moshi-jsonapi-retrofit-converter:<version>' // optional, for retrofit
}
```

For library version >= 3.5, moshi is removed from runtime dependencies of the library to become a peer dependency.

Use snapshot version:

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.squareup.moshi:moshi:1.4.0'
    implementation 'moe.banana:moshi-jsonapi:master-SNAPSHOT'
}
```

NOTE: It's necessary clean gradle library cache to access the latest snapshot version.

## Proguard Guide

For moshi-jsonapi:

```
-keepattributes Signature
-keepclassmembers public abstract class moe.banana.jsonapi2.** {
    *;
}
```

For moshi, if you use a custom JSON adapter (e.g. for Enum types):

```
-keepclassmembers class ** {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}
```

## Supported Features

| Feature                        | Supported | Note                                            |
| ------------------------------ | --------- | ----------------------------------------------- |
| Serialization                  | Yes       |                                                 |
| Deserialization                | Yes       |                                                 |
| Custom-named fields            | Yes       | With `@Json`                                    |
| Top level errors               | Yes       |                                                 |
| Top level metadata             | Yes       |                                                 |
| Top level links                | Yes       |                                                 |
| Top level JSON API Object      | Yes       |                                                 |
| Resource metadata              | Yes       |                                                 |
| Resource links                 | Yes       |                                                 |
| Relationships                  | Yes       | `HasOne` and `HasMany`                          |
| Inclusion of related resources | Yes       |                                                 |
| Resource IDs                   | Yes       |                                                 |

## Migration Note for 3.4 and 3.5

Release 3.4 removed type parameter from `Document` object which can break your code. Please replace the type declaration with
`ObjectDocument<T>` or `ArrayDocument<T>` if you insist that.

Release 3.5 changes the dependency to moshi from runtime dependency to compile-only dependency, which means moshi-jsonapi does no longer
includes moshi as a dependency for your project. And you need to add moshi to the dependencies of the project manually.

## Migration from 2.x to 3.x

3.x supports all features supported by JSON API specification. And the interface changed a lot especially in serialization/deserialization.
More object oriented features are added to new API. If you're using the library with Retrofit, migration should be a lot easier by using a
special `Converter` adapts `Document<Article>` to `Article[]` and backward as well (see [retrofit section](#retrofit)). Migration should be
easy if you use latest 2.x API with some OO features already available. Otherwise, it can take hours to migrate to new API.

## Migration from 1.x to 2.x

2.x abandoned much of seldomly used features of JSON API specification and re-implement the core of JSON API without
AutoValue since AutoValue is considered too verbose to implement a clean model.

And the new API no longer requires a verbose null check since you should take all control over the POJO model's nullability check.

Another major change is that the new API is not compatible with AutoValue any more. Means that one have to choose 1.x implementation
if AutoValue is vital to bussiness logic.

## License

(The MIT License)
