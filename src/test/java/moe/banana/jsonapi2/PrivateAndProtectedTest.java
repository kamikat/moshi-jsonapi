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
        assertEquals("a", data.getA());
        assertEquals(1.23, data.getB(), 0.000000001);
        assertEquals((Integer) 123, data.getC());
        assertEquals(true, data.getD());
    }

    @Test
    public void deserializePrivateHasOneAuthor() throws Exception {
        Private data = privateAdapter().fromJson(getPrivateSample());
        assertEquals("9", data.getAuthor().linkage.id);
    }

    @Test
    public void deserializePrivateHasManyReaders() throws Exception {
        Private data = privateAdapter().fromJson(getPrivateSample());
        assertEquals("5", data.getReaders().linkages[0].id);
        assertEquals("12", data.getReaders().linkages[1].id);
    }

    @Test
    public void deserializeTransientFieldShouldBeIgnored() throws Exception {
        Private data = privateAdapter().fromJson(getPrivateSample());
        assertNull(data.getIgnored());
    }

    @Test
    public void deserializeInheritedPrivatePrimitives() throws Exception {
        InheritedPrivate data = inheritedPrivateAdapter().fromJson(getPrivateSample());
        assertEquals("a", data.getA());
        assertEquals(1.23, data.getB(), 0.000000001);
        assertEquals((Integer) 123, data.getC());
        assertEquals(true, data.getD());
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
        assertEquals("a", data.getA());
        assertEquals(1.23, data.getB(), 0.000000001);
        assertEquals((Integer) 123, data.getC());
        assertEquals(true, data.getD());
    }

    @Test
    public void deserializeProtectedHasOneAuthor() throws Exception {
        Protected data = protectedAdapter().fromJson(getPrivateSample());
        assertEquals("9", data.getAuthor().linkage.id);
    }

    @Test
    public void deserializeProtectedHasManyReaders() throws Exception {
        Protected data = protectedAdapter().fromJson(getPrivateSample());
        assertEquals("5", data.getReaders().linkages[0].id);
        assertEquals("12", data.getReaders().linkages[1].id);
    }

    @Test
    public void serializeProtected() throws Exception {
        Protected data = generateProtected();
        String expected = TestResources.getPrivateSampleSerialized();
        assertEquals(expected, protectedAdapter().toJson(data));
    }

    private Private generatePrivate() {
        Private result = new Private();
        result.setA("a");
        result.setB(1.23);
        result.setC(123);
        result.setD(true);

        Person author = new Person();
        author._id = "author";

        Person firstReader = new Person();
        firstReader._id = "firstReader";
        Person secondReader = new Person();
        secondReader._id = "secondReader";

        result.setAuthor(HasOne.create(result, author));
        result.setReaders(HasMany.create(result, firstReader, secondReader));

        result.setIgnored("ignored");

        return result;
    }

    private Protected generateProtected() {
        Protected result = new Protected();
        result.setA("a");
        result.setB(1.23);
        result.setC(123);
        result.setD(true);

        Person author = new Person();
        author._id = "author";

        Person firstReader = new Person();
        firstReader._id = "firstReader";
        Person secondReader = new Person();
        secondReader._id = "secondReader";

        result.setAuthor(HasOne.create(result, author));
        result.setReaders(HasMany.create(result, firstReader, secondReader));

        result.setIgnored("ignored");

        return result;
    }

    private Resource.Adapter<Private> privateAdapter() {
        return adapter(Private.class);
    }

    private Resource.Adapter<Protected> protectedAdapter() {
        return adapter(Protected.class);
    }

    private Resource.Adapter<InheritedPrivate> inheritedPrivateAdapter() {
        return adapter(InheritedPrivate.class);
    }

    public static <T extends Resource> Resource.Adapter<T> adapter(Class<T> clazz) {
        return new Resource.Adapter<T>(clazz, moshi());
    }

    private static Moshi moshi() {
        Moshi.Builder builder = new Moshi.Builder();
        builder.add(ResourceAdapterFactory.builder()
                .add(Person.class)
                .build());
        return builder.build();
    }

}
