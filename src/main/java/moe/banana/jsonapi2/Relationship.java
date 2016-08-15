package moe.banana.jsonapi2;

interface Relationship<T> {

    /**
     * @return linked resource object
     */
    T get() throws ResourceNotFoundException;
}
