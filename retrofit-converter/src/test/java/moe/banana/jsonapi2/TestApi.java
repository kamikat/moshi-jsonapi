package moe.banana.jsonapi2;

import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Comment;
import retrofit2.Call;
import retrofit2.http.*;

public interface TestApi {

    @GET("articles")
    Call<Article[]> listArticles();

    @GET("articles/{id}")
    Call<Article> getArticle(@Path("id") String id);

    @GET("articles/{id}/comments")
    Call<Comment[]> getComments(@Path("id") String id);

    @POST("articles/{id}/comments")
    Call<Document> addComment(@Path("id") String id, @Body Comment comment);

    @PUT("articles/{id}/relationships/author")
    Call<Document> updateAuthor(@Path("id") String id, @Body ResourceIdentifier authorLinkage);

    @GET("articles/{id}/relationships/tags")
    Call<ResourceIdentifier[]> getRelTags(@Path("id") String id);

}
