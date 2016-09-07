# moshi-jsonapi

[![Build Status](https://travis-ci.org/kamikat/moshi-jsonapi.svg?branch=master)](https://travis-ci.org/kamikat/moshi-jsonapi)
[![Coverage Status](https://coveralls.io/repos/github/kamikat/moshi-jsonapi/badge.svg?branch=master)](https://coveralls.io/github/kamikat/moshi-jsonapi?branch=master)
[![Release](https://jitpack.io/v/moe.banana/moshi-jsonapi.svg)](https://jitpack.io/#moe.banana/moshi-jsonapi)

Java implementation of [JSON API](http://jsonapi.org/) Specification v1.0 for [moshi](https://github.com/square/moshi).

Create a Moshi adapter factory from resource object classes:

```java
JsonAdapter.Factory jsonApiAdapterFactory = ResourceAdapterFactory.builder()
        .add(Article.class)
        .add(Person.class)
        .add(Comment.class)
        .build();
```

Add factory to Moshi instance builder:

```java
Moshi moshi = new Moshi.Builder()
        .add(jsonApiAdapterFactory)
        .build();
```

Deserialize object from JSON string using Moshi:

```
String json = "...";
Article[] articles = moshi.adapter(Articles[].class).fromJson(json);
System.out.println(articles[0].title);
```

### Resource Object

Extend a `Resource` class to create a resource object class.
The class **must** be annotated with `@JsonApi(type = ...)`.

```java
@JsonApi(type = "people")
class Person extend Resource {
    @Json(name="first-name") String firstName;
    @Json(name="last-name") String lastName;
    String twitter;
}
```

Serialize the resource:

```java
Person person = new Person();
person._id = "1";
person.firstName = "Yuki";
person.lastName = "Kiriyama";
person.twitter = "kamikat_bot";
moshi.adapter(Person.class).toJson(person);
// => { "type": "people", "attributes": { "first-name": "Yuki", "last-name": "Kiriyama", "twitter": "kamikat_bot" } }
```

### Relationship

The library supports two types of relationships: `HasOne<? extends Resource>` and `HasMany<? extends Resource>`
each of which has a single type parameter to declaring type of linked resource object.

```java
@JsonApi(type = "articles")
public class Article extends Resource {
    public String title;
    public HasOne<Person> author;
    public HasMany<Comment> comments;
}
```

Relationships can be resolved to resource object if the resource belongs to a document object:

```java
Article article = moshi.adapter(Article.class).fromJson("{ data: ..., included: [...] }");
Person author = article.author.get();
```

`HasOne.get()` throws a `ResourceNotFoundException` if there is no matching resource in document.

Serialize the resource:

```java
Article article = new Article();
article.title = "Little Brown Fox";
article.author = HasOne.create(article, author);
moshi.adapter(Article.class).toJson(article);
// => { "type": "articles", "relationships": { "author": { "data": "type": "people", id: "1" } } }
```

### Document

Put them together to get a full document:

```java
Document document = Document.of(article);
document.addInclude(author);
moshi.adapter(Article.class).toJson(article);
// => {
//      data: { "type": "articles", "relationships": { "author": { "data": "type": "people", id: "1" } } },
//      included: [
//        { "type": "people", "attributes": { "first-name": "Yuki", "last-name": "Kiriyama", "twitter": "kamikat_bot" } }
//      ]
//    }
```

### Retrofit

Integrate with Retrofit in a minute:

```java
interface MyService {
    Call<Article[]> listArticles();
    Call<Article> newArticle(@Body Article article);
}
```

```java
MyService service = retrofit.create(MyService.class);
service.listArticles(); // => Call<Article[]>
service.newArticle(article); // => Call<Article>
```

### Strict Mode

By default, the adapter will parse unknown type as a `Resource` object without any fields.
Use `strict` flag to enforce a `JsonDataException` when it reads an unknown type of resource.

```java
JsonAdapter.Factory jsonApiAdapterFactory = ResourceAdapterFactory.builder()
        ...
        .strict() // enables strict mode
        .build();
```

## Download

Add repository to Gradle build file:

    repositories {
        maven { url "https://jitpack.io" }
    }

Add the dependency:

    dependencies {
        compile 'moe.banana:moshi-jsonapi:<version>'
    }

## Migration from 1.x

2.x abandoned much of seldomly used features of JSON API specification and re-implement the core of JSON API without
AutoValue since AutoValue is considered too verbose to implement a clean model.

And the new API no longer requires a verbose null check since you should take all control over the POJO model's nullability check.

Another major change is that the new API is not compatible with AutoValue any more. Means that one have to choose 1.x implementation
if AutoValue is vital to bussiness logic.

## License

(The MIT License)
