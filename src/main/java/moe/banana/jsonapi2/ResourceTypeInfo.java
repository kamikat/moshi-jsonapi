package moe.banana.jsonapi2;

class ResourceTypeInfo<T extends Resource> {

    Class<T> type;
    Class<T[]> arrayType;
    JsonApi jsonApi;

    @SuppressWarnings("unchecked")
    ResourceTypeInfo(Class<T> type) {
        this.type = type;
        try {
            this.arrayType = (Class<T[]>) Class.forName("[L" + type.getName() + ";");
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
        this.jsonApi = type.getAnnotation(JsonApi.class);
    }

}
