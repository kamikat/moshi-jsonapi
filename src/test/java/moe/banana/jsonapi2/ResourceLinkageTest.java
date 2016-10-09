package moe.banana.jsonapi2;

import org.junit.Test;

import moe.banana.jsonapi2.model.Person;

import static org.junit.Assert.assertEquals;

public class ResourceLinkageTest {

    @Test
    public void equality() throws Exception {
        assertEquals(createLinkage(), createLinkage());
    }

    @Test
    public void equality_same_resource() throws Exception {
        assertEquals(ResourceLinkage.of(createPerson()), ResourceLinkage.of(createPerson()));
    }

    @Test
    public void hashcodes_equal() throws Exception {
        assertEquals(createLinkage().hashCode(), createLinkage().hashCode());
    }

    private Person createPerson() {
        Person person = new Person();
        person._id = "personId";
        return person;
    }

    private ResourceLinkage createLinkage() {
        return ResourceLinkage.of("people", "personId");
    }

}
