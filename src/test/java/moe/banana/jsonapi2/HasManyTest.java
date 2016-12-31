package moe.banana.jsonapi2;

import moe.banana.jsonapi2.model.Comment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class HasManyTest {

    @Test
    public void equals_for_empty_array() throws Exception {
        assertEquals(comments(0), comments(0));
    }

    @Test
    public void equals_for_multiple() throws Exception {
        assertEquals(comments(1), comments(1));
        assertEquals(comments(2), comments(2));
    }

    @Test
    public void hashcode_for_empty_array() throws Exception {
        assertEquals(comments(0).hashCode(), comments(0).hashCode());
    }

    @Test
    public void hashcode_for_multiple() throws Exception {
        assertEquals(comments(1).hashCode(), comments(1).hashCode());
        assertEquals(comments(2).hashCode(), comments(2).hashCode());
    }

    private HasMany<Comment> comments(int size) {
        ArrayList<Comment> comments = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Comment comment = new Comment();
            comment.setId("comment" + i);
            comments.add(comment);
        }
        return new HasMany<>(comments.toArray(new Comment[size]));
    }

}
