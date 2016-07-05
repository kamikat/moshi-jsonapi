moshi-jsonapi
-------------

[![Build Status](https://travis-ci.org/kamikat/moshi-jsonapi.svg?branch=master)](https://travis-ci.org/kamikat/moshi-jsonapi)
[![Release](https://jitpack.io/v/moe.banana/moshi-jsonapi.svg)](https://jitpack.io/#moe.banana/moshi-jsonapi)

Java implementation of [JSON API](http://jsonapi.org/) Specification v1.0 for [moshi](https://github.com/square/moshi).

```java
String json = ...;

Moshi moshi = new Moshi.Builder()
    .add(JsonApiFactory.create(Article.class, Comment.class, People.class)) // Setup JSON API adapter factory
    .build();

Document document = moshi.adapter(Document.class).fromJson(json);

...;

System.out.println(document);
```

Usage
-----

### Install ###

**Step 1** Add the JitPack repository to your Gradle build file:

		allprojects {
				repositories {
						...
						maven { url "https://jitpack.io" }
				}
		}

**Step 2** Add the dependency:

		dependencies {
				compile 'moe.banana:moshi-jsonapi:<version>'
		}

Or install Android version with `Parcelable` support:

		dependencies {
				compile 'moe.banana:moshi-jsonapi:<version>-android'
		}

### API ###

#### Attributes Object ####

An attributes object describe fields in `attributes` of a resource object.

```java
@AttributesObject(type = "people")
class People {
    @Json(name="first-name") String firstName;
    @Json(name="last-name") String lastName;
    String twitter;
}
```

The `@AttributesObject` annotation containing `type` of the attributes object is **required** for attributes objects.

And, all attributes object class **must** be declared in `JsonApiFactory.create` call:

```java
builder.add(JsonApiFactory.create(Article.class, Comment.class, People.class))
```

The resource adapter reads `type` attribute from resource object to deterimine which resource is being parsed,
and obtain an adapter for `People.class` from moshi to do the deserialization stuff of `attributes` field.

Custom serialization/deserialization of attributes object is supported by moshi (with `builder.add` calls).

#### Resource Object ####

When document's primary data is a representation of a single resource:

```java
Resource resource = document.data();

resource.type()          // => String
resource.id()            // => String
resource.attributes()    // => Object, or resource.attrs<T>() -> T
resource.relationships() // => Map<String, Relationship>
resource.links()         // => Links
```

(you may prefer `resource.attrs()` over `resource.attributes()` which performs a cast to object for you)

Or, a group of resource:

```java
Resource resources = document.data();

for (Resource resource : resources) {
    ...;
}
```

(Access `Resource` as a single resource on a group of resource can result in `InvalidAccessException`)

#### Create Resource Object ####

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

#### Document Object ####

Document object prases a [top-level object](http://jsonapi.org/format/#document-top-level) which must contains at least one of:

- `data`: the document’s “primary data”, can be a resource object or array of resource objects
- `errors`: an array of error objects
- `meta`:  a meta object that contains non-standard meta-information

and following optional fields:

- `jsonapi`: an object describing the server’s implementation
- `links`: a links object related to the primary data.
- `included`: an array of resource objects that are related to the primary data and/or each other (“included resources”).

### AutoValue Integration ###

The library is built upon [AutoValue](https://github.com/google/auto/tree/master/value) and [auto-value-moshi](https://github.com/rharter/auto-value-moshi).
Although AutoValue is not required to use this library, it's strongly recommended to built clean model with google auto.
See [test](src/test/java/moe/banana/jsonapi/test) for implementation details of AutoValue integration.

ProGuard
--------

`auto-value-moshi` generates reflective code, and we need following snippet to be added in proguard configuration:

    -keepattributes Signature
    -keepclassmembers public abstract class moe.banana.jsonapi.** {
       public abstract <methods>;
    }

Todos
-----

- [ ] Patch update
- [ ] Null check is soooooo verbose

Example
-------

Here is a formatted result of `document.toString()` on <https://jsonapi.org>.

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
