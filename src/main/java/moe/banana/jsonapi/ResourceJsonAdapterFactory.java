package moe.banana.jsonapi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class ResourceJsonAdapterFactory implements JsonAdapter.Factory {

    Map<String, Type> mTypeMap;
    Map<Type, JsonAdapter.Factory> mFactoryMap;

    @Override
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        JsonAdapter<?> plainAdapter = AutoValue_ResourceJsonAdapter_PlainResource.typeAdapterFactory().create(type, annotations, moshi);
        if (plainAdapter != null) {
            return plainAdapter;
        }
        if (type == Resource.class) {
            return new ResourceJsonAdapter(mTypeMap, moshi);
        }
        if (mFactoryMap.containsKey(type)) {
            return mFactoryMap.get(type).create(type, annotations, moshi);
        }
        return null;
    }

    public ResourceJsonAdapterFactory(Class<?>... classes) {
        processAttributesAnnotation(classes);
    }

    void processAttributesAnnotation(Class<?>[] classes) {
        Map<String, Type> types = new HashMap<>(classes.length);
        Map<Type, JsonAdapter.Factory> factories = new HashMap<>(classes.length);
        for (Class<?> cls : classes) {
            AttributesObject attributes = cls.getAnnotation(AttributesObject.class);
            if (attributes == null) {
                throw new AssertionError("ResourceJsonAdapter requires class with @AttributesObject annotation.");
            }
            types.put(attributes.type(), cls);
            try {
                Constructor<? extends JsonAdapter.Factory> constructor = attributes.factory().getConstructor();
                constructor.setAccessible(true);
                factories.put(cls, constructor.newInstance());
            } catch (NoSuchMethodException e) {
                throw new AssertionError(attributes.factory().getSimpleName() + " must have a default constructor.");
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        mTypeMap = types;
        mFactoryMap = factories;
    }

}
