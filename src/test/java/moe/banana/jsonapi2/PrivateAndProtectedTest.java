package moe.banana.jsonapi2;

import com.squareup.moshi.Moshi;

import org.junit.Test;

import moe.banana.jsonapi2.model.InheritedPrivate;
import moe.banana.jsonapi2.model.Person;
import moe.banana.jsonapi2.model.Private;
import moe.banana.jsonapi2.model.Protected;

import static moe.banana.jsonapi2.TestResources.getPrivateSample;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PrivateAndProtectedTest {

    @Test
    public void deserializePrivatePrimitives() throws Exception {
        Private data = privateAdapter().fromJson(getPrivateSample());
        assertEquals("a", data.getSomeString());
        assertEquals(1.23, data.getSomeDouble(), 0.000000001);
        assertEquals((Integer) 123, data.getSomeInteger());
        assertEquals(true, data.getSomeBoolean());
    }

    @Test
    public void deserializePrivateHasOneAuthor() throws Exception {
        Private data = privateAdapter().fromJson(getPrivateSample());
        assertEquals("9", data.getAuthor().get().getId());
    }

    @Test
    public void deserializePrivateHasManyReaders() throws Exception {
        Private data = privateAdapter().fromJson(getPrivateSample());
        assertEquals("5", data.getReaders().get(0).getId());
        assertEquals("12", data.getReaders().get(1).getId());
    }

    @Test
    public void deserializeTransientFieldShouldBeIgnored() throws Exception {
        Private data = privateAdapter().fromJson(getPrivateSample());
        assertNull(data.getIgnored());
    }

    @Test
    public void deserializeInheritedPrivatePrimitives() throws Exception {
        InheritedPrivate data = inheritedPrivateAdapter().fromJson(getPrivateSample());
        assertEquals("a", data.getSomeString());
        assertEquals(1.23, data.getSomeDouble(), 0.000000001);
        assertEquals((Integer) 123, data.getSomeInteger());
        assertEquals(true, data.getSomeBoolean());
    }

    @Test
    public void serializePrivate() throws Exception {
        Private data = generatePrivate();
        String expected = TestResources.getPrivateSampleSerialized();
        assertEquals(expected, privateAdapter().toJson(data));
    }

    @Test
    public void deserializeProtectedPrimitives() throws Exception {
        Protected data = protectedAdapter().fromJson(getPrivateSample());
        assertEquals("a", data.getSomeString());
        assertEquals(1.23, data.getSomeDouble(), 0.000000001);
        assertEquals((Integer) 123, data.getSomeInteger());
        assertEquals(true, data.getSomeBoolean());
    }

    @Test
    public void deserializeProtectedHasOneAuthor() throws Exception {
        Protected data = protectedAdapter().fromJson(getPrivateSample());
        assertEquals("9", data.getAuthor().get().getId());
    }

    @Test
    public void deserializeProtectedHasManyReaders() throws Exception {
        Protected data = protectedAdapter().fromJson(getPrivateSample());
        assertEquals("5", data.getReaders().get(0).getId());
        assertEquals("12", data.getReaders().get(1).getId());
    }

    @Test
    public void serializeProtected() throws Exception {
        Protected data = generateProtected();
        String expected = TestResources.getPrivateSampleSerialized();
        assertEquals(expected, protectedAdapter().toJson(data));
    }

    private Private generatePrivate() {
        Private result = new Private();
        result.setSomeString("a");
        result.setSomeDouble(1.23);
        result.setSomeInteger(123);
        result.setSomeBoolean(true);

        Person author = new Person();
        author.setId("author");

        Person firstReader = new Person();
        firstReader.setId("firstReader");
        Person secondReader = new Person();
        secondReader.setId("secondReader");

        result.setAuthor(new HasOne<>(author));
        result.setReaders(new HasMany<>(firstReader, secondReader));

        result.setIgnored("ignored");

        return result;
    }

    private Protected generateProtected() {
        Protected result = new Protected();
        result.setSomeString("a");
        result.setSomeDouble(1.23);
        result.setSomeInteger(123);
        result.setSomeBoolean(true);

        Person author = new Person();
        author.setId("author");

        Person firstReader = new Person();
        firstReader.setId("firstReader");
        Person secondReader = new Person();
        secondReader.setId("secondReader");

        result.setAuthor(new HasOne<>(author));
        result.setReaders(new HasMany<>(firstReader, secondReader));

        result.setIgnored("ignored");

        return result;
    }

    private ResourceAdapter<Private> privateAdapter() {
        return adapter(Private.class);
    }

    private ResourceAdapter<Protected> protectedAdapter() {
        return adapter(Protected.class);
    }

    private ResourceAdapter<InheritedPrivate> inheritedPrivateAdapter() {
        return adapter(InheritedPrivate.class);
    }

    public static <T extends Resource> ResourceAdapter<T> adapter(Class<T> clazz) {
        return new ResourceAdapter<T>(clazz, moshi());
    }

    private static Moshi moshi() {
        Moshi.Builder builder = new Moshi.Builder();
        builder.add(ResourceAdapterFactory.builder()
                .add(Person.class)
                .build());
        return builder.build();
    }

}
