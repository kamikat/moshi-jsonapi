package moe.banana.jsonapi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

public class JsonApiAdapterFactory implements JsonAdapter.Factory {

    JsonAdapter.Factory[] mFactoryArray = new JsonAdapter.Factory[] {
            new JsonAdapter.Factory() {
                @Override
                public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
                    if (type == Link.class) {
                        return new LinkJsonAdapter(moshi);
                    }
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType() == Implicit.class) {
                            if (type == Link.class) {
                                return new LinkJsonAdapter(moshi);
                            }
                        }
                        if (annotation.annotationType() == OneOrMany.class) {
                            if (type == Resource.class) {
                                return new OneOrManyJsonAdapter<Resource>(Resource.class, new OneOrManyJsonAdapter.ContainerFactory<Resource>() {
                                    @Override
                                    public Resource createContainer() {
                                        return new Resources();
                                    }
                                }, moshi);
                            }
                            if (type == ResourceLinkage.class) {
                                return new OneOrManyJsonAdapter<ResourceLinkage>(ResourceLinkage.class, new OneOrManyJsonAdapter.ContainerFactory<ResourceLinkage>() {
                                    @Override
                                    public ResourceLinkage createContainer() {
                                        return new ResourceLinkages();
                                    }
                                }, moshi);
                            }
                        }
                    }
                    return null;
                }
            },
            AutoValue_Document.typeAdapterFactory(),
            AutoValue_Document_Links.typeAdapterFactory(),
            AutoValue_Error.typeAdapterFactory(),
            AutoValue_Error_Links.typeAdapterFactory(),
            AutoValue_Error_Source.typeAdapterFactory(),
            AutoValue_JsonApi.typeAdapterFactory(),
            AutoValue_Link.typeAdapterFactory(),
            AutoValue_Links.typeAdapterFactory(),
            AutoValue_Relationship.typeAdapterFactory(),
            AutoValue_Relationship_Links.typeAdapterFactory(),
            AutoValue_ResourceLinkage.typeAdapterFactory(),
    };

    @Override
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        for (JsonAdapter.Factory factory: mFactoryArray) {
            JsonAdapter<?> adapter = factory.create(type, annotations, moshi);
            if (adapter != null) {
                return adapter;
            }
        }
        return null;
    }

}
