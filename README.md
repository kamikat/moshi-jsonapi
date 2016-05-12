moshi-jsonapi
-------------

[![Build Status](https://travis-ci.org/kirisetsz/moshi-jsonapi.svg?branch=master)](https://travis-ci.org/kirisetsz/moshi-jsonapi)

Java implementation of [JSON API](http://jsonapi.org/) Specification v1.0 built on [moshi](https://github.com/square/moshi).

```java
String json = ...;

Moshi moshi = new Moshi.Builder()
    .add(JsonApiFactory.create(Article.class, Comment.class, People.class)) // Setup JSON API document adapter
    .build();

Document document = moshi.adapter(Document.class).fromJson(json);

...;

System.out.println(document);
```

### Setup Attributes Object ###

An attributes object represents `attributes` value of resource object in JSON API.

```java
@AttributesObject(type = "people")
class People {
    @Json(name="first-name") String firstName;
    @Json(name="last-name") String lastName;
    String twitter;
}
```

All attributes object **must** be declared in `JsonApiFactory.create` call:

```java
builder.add(JsonApiFactory.create(Article.class, Comment.class, People.class))
```

The `@AttributesObject` annotation containing `type` of the attributes object is required for attributes objects.
When a `people` resource is being parsed, the adapter to `People.class` is requested from moshi and doing the deserialization of `attributes` field.

Custom serialization/deserialization of attributes object is supported in native moshi approach (with `builder.add` calls).

### Document Object ###

Document object prases a [top-level object](http://jsonapi.org/format/#document-top-level) which must contains at least one of:

- `data`: the document’s “primary data”
- `errors`: an array of error objects
- `meta`:  a meta object that contains non-standard meta-information

and following optional fields:

- `jsonapi`: an object describing the server’s implementation
- `links`: a links object related to the primary data.
- `included`: an array of resource objects that are related to the primary data and/or each other (“included resources”).

to access these fields.

```java
document.data()     // => <Resource Object> | [ <Resource Object> ]
document.errors()   // => [ <Error Object> ]
document.meta()     // => Object
document.jsonapi()  // => <JsonApi Object>
document.links()    // => <Links Object>
document.included() // => [ <Resource Object> ]
```

### Resource Object ###

If document's primary data is a representation of a single resource:

```java
Resource resource = document.data();

resource.type()          // => String
resource.id()            // => String
resource.attributes()    // => Object, or resource.attrs<T>() -> T
resource.relationships() // => Map<String, Relationship>
resource.links()         // => Links
```

`resource.attrs()` is a shortcut to `resource.attributes()` casting attributes object to type expected by a caller
(explicit type parameter may required in case of ambiguous context).

or, a group of resource

```java
Resource resources = document.data();

for (Resource resource : resources) {
    ...;
}
```

(Access `Resource` as a single resource on a group of resource can result in `InvalidAccessException`)

### Creating Resource Object ###

Create single resource object:

```java
Resource resource = Resource.builder()
        .type("people")
        .attributes(attributesObject)
        .relationships(relationshipsObject)
        .build();

System.out.println(moshi.adapter(Resource.class).toJson(resource));
```

group of resource object:

```java
Resource resources = new Resources()
        .append(resource1, resource2, resource3, ...)
        .append(resource4);

resources.add(resource5);

System.out.println(resources.size());
```

### AutoValue Integration ###

The library is built upon [AutoValue](https://github.com/google/auto/tree/master/value) and [auto-value-moshi](https://github.com/rharter/auto-value-moshi).
Although AutoValue is not required to use this library, it's strongly recommended to built clean model with google auto.
See [test](src/test/java/moe/banana/jsonapi/test) for implementation details of AutoValue integration.

Download
--------

Download [latest jar](https://jcenter.bintray.com/moe/banana/jsonapi/moshi-jsonapi/) or depend via Maven

    <dependency>
      <groupId>moe.banana.jsonapi</groupId>
      <artifactId>moshi-jsonapi</artifactId>
      <version>1.0.4</version>
      <type>pom</type>
    </dependency>

or Gradle

    compile 'moe.banana.jsonapi:moshi-jsonapi:1.0.4'

Todos
-------

- [ ] Support patch update

Example
-------

Here is a formatted result of `document.toString()`.

    Document{
      data=[
        Resource{
          meta=null,
          type=articles,
          id=1,
          attributes=Article{
            title=JSON API paints my bikeshed!
          },
          relationships={
            author=Relationship{
              meta=null,
              links=Links{
                self=Link{meta=null, href=http://example.com/articles/1/relationships/author},
                related=Link{meta=null, href=http://example.com/articles/1/author}
              },
              data=ResourceLinkage{
                meta=null, type=people, id=9
              }
            },
            comments=Relationship{
              meta=null,
              links=Links{
                self=Link{meta=null, href=http://example.com/articles/1/relationships/comments},
                related=Link{meta=null, href=http://example.com/articles/1/comments}
              },
              data=[
                ResourceLinkage{meta=null, type=comments, id=5},
                ResourceLinkage{meta=null, type=comments, id=12}
              ]
            }
          },
          links=Links{
            self=Link{meta=null, href=http://example.com/articles/1}
          }
        }
      ],
      errors=null,
      meta=null,
      links=Links{
        self=Link{meta=null, href=http://example.com/articles},
        first=null,
        last=Link{meta=null, href=http://example.com/articles?page[offset]=10},
        prev=null,
        next=Link{meta=null, href=http://example.com/articles?page[offset]=2}
      },
      included=[
        Resource{
          meta=null,
          type=people,
          id=9,
          attributes=People{
            firstName=Dan,
            lastName=Gebhardt,
            twitter=dgeb
          },
          relationships=null,
          links=Links{
            self=Link{meta=null, href=http://example.com/people/9}
          }
        },
        Resource{
          meta=null,
          type=comments,
          id=5,
          attributes=Comment{
            body=First!
          },
          relationships={
            author=Relationship{
              meta=null,
              links=null,
              data=ResourceLinkage{meta=null, type=people, id=2}
            }
          },
          links=Links{
            self=Link{meta=null, href=http://example.com/comments/5}
          }
        },
        Resource{
          meta=null,
          type=comments,
          id=12,
          attributes=Comment{
            body=I like XML better
          },
          relationships={
            author=Relationship{
              meta=null,
              links=null,
              data=ResourceLinkage{meta=null, type=people, id=9}
            }
          },
          links=Links{
            self=Link{meta=null, href=http://example.com/comments/12}
          }
        }
      ],
      jsonapi=null
    }

License
-------

(The MIT License)
