package moe.banana.jsonapi2;

class ResourceTypeInfo<T extends Resource> {

    Class<T> type;
    Class<T[]> arrayType;
    JsonApi jsonApi;

    @SuppressWarnings("unchecked")
    ResourceTypeInfo(Class<T> type) throws ClassNotFoundException {
        this.type = type;
        this.arrayType = (Class<T[]>) Class.forName("[L" + type.getName() + ";");
        this.jsonApi = type.getAnnotation(JsonApi.class);
    }

}
