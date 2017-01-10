# moshi-jsonapi

[![Build Status](https://travis-ci.org/kamikat/moshi-jsonapi.svg?branch=master)](https://travis-ci.org/kamikat/moshi-jsonapi)
[![Coverage Status](https://coveralls.io/repos/github/kamikat/moshi-jsonapi/badge.svg?branch=master)](https://coveralls.io/github/kamikat/moshi-jsonapi?branch=master)
[![Release](https://jitpack.io/v/moe.banana/moshi-jsonapi.svg)](https://jitpack.io/#moe.banana/moshi-jsonapi)

Java implementation of [JSON API](http://jsonapi.org/) specification v1.0 for [moshi](https://github.com/square/moshi).

## Setup

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
Type type = Types.newParameterizedType(Document.class, Article.class); // Document<Article>
JsonAdapter<Document<Article>> adapter = ((JsonAdapter<Document<Article>>) moshi.adapter(type));
Document<Article> articles = adapter.fromJson(json);
for (Article article : articles) {
  System.out.println(article.title);
}
```

### Retrofit

Simply add a [retrofit converter](https://gist.github.com/kamikat/baa7d086f932b0dc4fc3f9f02e37a485) and you get all the
cool stuff in Retrofit!

```java
public interface MyAPI {

    @GET("posts")
    Call<Post[]> listPosts();

    @GET("posts/{id}")
    Call<Post> getPost(@Path("id") String id);

    @GET("posts/{id}/comments")
    Call<Comment[]> getComments(@Path("id") String id);

    @POST("posts/{id}/comments")
    Call<Document> addComment(@Path("id") String id, @Body Comment comment);

    @DELETE("posts/{id}/relationships/comments")
    Call<Document> removeComments(@Path("id") String id, @Body ResourceIdentifier[] commentIds);

    @GET("posts/{id}/relationships/comments")
    Call<ResourceIdentifier[]> getCommentRels(@Path("id") String id);
}
```

No annoying `Call<Document<RESOURCE>>` declaration is required as `Document` is wrap/unwrapped automatically by the converter.
Of course, you can just declare them if you need `Document` to collecting errors or any other information.

### Server Applications

All functions of moshi-jsonapi can actually work on server running Java platform.

## Modelling

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
Person author = article.author.get(article.getContext());
```

You can use `Resource.getContext()` to access the `Document` object the `Resource` be added/included in.
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
        return author.get(getContext());
    }

    public List<Comment> getComments() {
        return comments.get(getContext());
    }
}
```

### Document

```java
Document<Article> document = new Document<>();
document.include(author);
document.set(article);

// Serialize
JsonAdapter<Document<Article>> adapter = moshi.adapter(document.getType());
System.out.println(adapter.toJson(document));
// => {
//      data: { "type": "articles", "relationships": { "author": { "data": "type": "people", id: "1" } } },
//      included: [
//        { "type": "people", "attributes": { "first-name": "Yuki", "last-name": "Kiriyama", "twitter": "kamikat_bot" } }
//      ]
//    }

// Deserialize
Document<Article> document2 = adapter.fromJson(...);
assert document2.get() instanceof Article
assert document2.get().getContext() == document2
```

All resources added/included into a `Document` will have a back-reference which can be accessed from `Resource.getContext`.

### Fallback

Deserialization will fail when processing an unknown type of resource.
Create a `default` typed model to avoid this problem and parses all unknown type of resource object into the default model.

```java
@JsonApi(type = "default")
class Unknown extends Resource {
    // nothing...
}
```

### meta/links/jsonapi Properties

You'd like to access `meta`/`links`/`jsonapi` value on `Document` for example.

```java
Document<Article> document = ...;
document.getMeta() // => JsonBuffer
```

As `meta` and `links` can contain a variant of objects, they are not been parsed when access with `getMeta` and `getLinks`.
You will get a `JsonBuffer` and you're expected to implement your `JsonAdapter<T>` to read/write these objects.

## Download

Add repository to gradle build file:

    repositories {
        maven { url "https://jitpack.io" }
    }

Add the dependency:

    dependencies {
        compile 'moe.banana:moshi-jsonapi:<version>'
    }

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
