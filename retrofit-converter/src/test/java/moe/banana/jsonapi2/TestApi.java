package moe.banana.jsonapi2;

import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Comment;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface TestApi {

    @GET("articles")
    Call<Article[]> listArticles();

    @GET("articles/{id}")
    Call<Article> getArticle(@Path("id") String id);

    @GET("articles/{id}/comments")
    Call<List<Comment>> getComments(@Path("id") String id);

    @PUT("articles/{id}/comments")
    Call<Document> addComments(@Path("id") String id, @Body List<Comment> comments);

    @POST("articles/{id}/comments")
    Call<Document> addComment(@Path("id") String id, @Body Comment comment);

    @PUT("articles/{id}/relationships/author")
    Call<Document> updateAuthor(@Path("id") String id, @Body ResourceIdentifier authorLinkage);

    @GET("articles/{id}/relationships/tags")
    Call<ResourceIdentifier[]> getRelTags(@Path("id") String id);

    @PUT("articles/{id}/relationships/tags")
    Call<Document> updateTags(@Path("id") String id, @Body ResourceIdentifier[] tagLinkages);

}
