package moe.banana.jsonapi2;

import org.junit.Test;

import moe.banana.jsonapi2.model.Person;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class ResourceIdentifierTest {

    private Document.ResourceReference createResourceReference() {
        Person person = new Person();
        person.setId("11");
        return new Document.ResourceReference(person);
    }

    private ResourceIdentifier createResourceIdentifier() {
        return new ResourceIdentifier("people", "11");
    }

    @Test
    public void equality_of_identifier_vs_identifier() throws Exception {
        ResourceIdentifier identifier = createResourceIdentifier();
        assertEquals(identifier, new ResourceIdentifier(identifier));
        assertEquals(identifier, createResourceIdentifier());
    }

    @Test
    public void equality_of_hashcode() throws Exception {
        assertEquals(createResourceIdentifier().hashCode(), createResourceIdentifier().hashCode());
    }

    @Test
    public void equality_of_identifier_vs_resource() throws Exception {
        assertEquals(createResourceIdentifier(), createResourceReference());
    }

    @Test
    public void equality_of_resource_vs_identifier() throws Exception {
        assertEquals(createResourceReference(), createResourceIdentifier());
    }

    @Test
    public void serialization() throws Exception {
        assertThat(TestUtil.moshi().adapter(ResourceIdentifier.class).toJson(createResourceIdentifier()),
                equalTo("{\"type\":\"people\",\"id\":\"11\"}"));
    }

    @Test
    public void deserialization() throws Exception {
        assertThat(TestUtil.moshi().adapter(ResourceIdentifier.class).fromJson("{\"type\":\"people\",\"id\":\"11\"}"),
                equalTo(createResourceIdentifier()));
    }

}
