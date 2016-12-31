package moe.banana.jsonapi2;

import org.junit.Test;

import moe.banana.jsonapi2.model.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ResourceIdentifierTest {

    @Test
    public void identifier_equality() throws Exception {
        assertEquals(createResourceIdentifier(), createResourceIdentifier());
    }

    @Test
    public void hashcode_equality() throws Exception {
        assertEquals(createResourceIdentifier().hashCode(), createResourceIdentifier().hashCode());
    }

    @Test
    public void identifier_equality_vs_resource() throws Exception {
        assertEquals(createResourceIdentifier(), createPerson());
    }

    @Test
    public void resource_equality_vs_identifier() throws Exception {
        assertNotEquals(createPerson(), createResourceIdentifier());
    }

    private Person createPerson() {
        Person person = new Person();
        person.setId("11");
        return person;
    }

    private ResourceIdentifier createResourceIdentifier() {
        return new ResourceIdentifier("people", "11");
    }

}
