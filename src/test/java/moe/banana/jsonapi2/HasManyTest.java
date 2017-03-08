package moe.banana.jsonapi2;

import moe.banana.jsonapi2.model.Comment;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@SuppressWarnings("all")
public class HasManyTest {

    private HasMany<Comment> comments(int size) {
        ArrayList<Comment> comments = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Comment comment = new Comment();
            comment.setId("" + i);
            comments.add(comment);
        }
        return new HasMany<Comment>(comments.toArray(new Comment[size]));
    }

    @Test
    public void equality() throws Exception {
        assertEquals(comments(0), comments(0));
        assertEquals(comments(1), comments(1));
        assertEquals(comments(2), comments(2));
    }

    @Test
    public void hashcode_equality() throws Exception {
        assertEquals(comments(0).hashCode(), comments(0).hashCode());
        assertEquals(comments(1).hashCode(), comments(1).hashCode());
        assertEquals(comments(2).hashCode(), comments(2).hashCode());
    }

    @Test
    public void resolution() {
        ObjectDocument document = new ObjectDocument();
        Comment holder = new Comment();
        assertThat(comments(2).get(document), hasItems(nullValue(Comment.class), nullValue(Comment.class)));
        assertThat(comments(2).get(document, holder), hasItems(holder, holder));
        Comment[] comments = new Comment[] { new Comment(), new Comment() };
        comments[0].setId("0");
        comments[1].setId("1");
        document.include(comments[0]);
        assertThat(comments(2).get(document), hasItems(equalTo(comments[0]), nullValue(Comment.class)));
        document.include(comments[1]);
        assertThat(comments(2).get(document), hasItems(comments[0], comments[1]));
    }

    @Test
    public void serialization() throws Exception {
        assertThat(TestUtil.moshi().adapter(HasMany.class).toJson(comments(2)),
                equalTo("{\"data\":[{\"type\":\"comments\",\"id\":\"0\"},{\"type\":\"comments\",\"id\":\"1\"}]}"));
    }

    @Test
    public void deserialization() throws Exception {
        assertThat(TestUtil.moshi().adapter(HasMany.class).fromJson("{\"data\":[{\"type\":\"comments\",\"id\":\"0\"},{\"type\":\"comments\",\"id\":\"1\"}]}"),
                equalTo((HasMany) comments(2)));
    }

}
