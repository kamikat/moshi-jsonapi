moshi-jsonapi
-------------

[![Build Status](https://travis-ci.org/kamikat/moshi-jsonapi.svg?branch=master)](https://travis-ci.org/kamikat/moshi-jsonapi)
[![Release](https://jitpack.io/v/moe.banana/moshi-jsonapi.svg)](https://jitpack.io/#moe.banana/moshi-jsonapi)

Java implementation of [JSON API](http://jsonapi.org/) Specification v1.0 for [moshi](https://github.com/square/moshi).

```java
String json = ...;

Moshi.Builder builder = new Moshi.Builder();
builder.add(ResourceAdapterFactory.builder()
        .add(Article.class)
        .add(Person.class)
        .add(Comment.class)
        .build());

Article[] articles = moshi.adapter(Articles[].class).fromJson(json);

...;
```

Usage
-----

### Install ###

Add the JitPack repository to Gradle build file:

    repositories {
        ...
        maven { url "https://jitpack.io" }
    }

Add the dependency:

    dependencies {
        compile 'moe.banana:moshi-jsonapi:<version>'
    }

### API ###

#### Resource Object ####

Extend a `Resource` class to define resource object.

```java
@JsonApi(type = "people")
class Person extend Resource {
    @Json(name="first-name") String firstName;
    @Json(name="last-name") String lastName;
    String twitter;
}
```

Annotate resource class with `@JsonApi(type = ...)` specifies type representing the class,
which is important in de-serialization. You can also specify `priority` attribute in case that
you have multiple implementation to a single type.

`Resource` object containing public fields `_id`/`_type` to describe the type and identifier of the resource.

Attributes are defined in `Resource` object as public read/writable fields.

```java
assert new Article()._type == "articles";
assert new Person()._type == "people";
```

**Important** don't forget add class to the `ResourceAdapterFactory` with Builder.
It is required for a polymorphic parse of resource objects.

#### Relationship ####

The library supports two types of relationships: `HasOne<? extends Resource>` and `HasMany<? extends Resource>`
each of which has a single type parameter to declaring the type of linked object.

```java
@JsonApi(type = "articles")
public class Article extends Resource {
    public String title;
    public HasOne<Person> author;
    public HasMany<Resource> comments;
}
```

Relationships can be resolved to resource object if the resource is parsed from a JSON API document object:

```java
Article article = moshi.adapter(Article.class).fromJson(...)
article.author.get() // => class Person
```

And array of resource objects:

```
article.comments.get() // => class Comment[]
```

### Migration from 1.x ###

2.x abandoned much of seldomly used features of JSON API specification and re-implement the core of JSON API without
AutoValue since AutoValue is considered too verbose to implement a clean model.

And the new API no longer requires a verbose null check since you should take all control over the POJO model's nullability check.

Another major change is that the new API is not compatible with AutoValue any more. Means that one have to choose 1.x implementation
if AutoValue is vital to bussiness logic.

## TODOs ##

- [ ] Permissive parsing (parse unrecognized resource)
- [ ] Error object

License
-------

(The MIT License)
