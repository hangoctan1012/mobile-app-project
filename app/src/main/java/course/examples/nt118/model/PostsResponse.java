package course.examples.nt118.model;

import java.util.List;

public class PostsResponse {
    private boolean success;
    private List<PostResponse> posts;
    private String nextCursor;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public List<PostResponse> getPosts() { return posts; }
    public void setPosts(List<PostResponse> posts) { this.posts = posts; }
    
    public String getNextCursor() { return nextCursor; }
    public void setNextCursor(String nextCursor) { this.nextCursor = nextCursor; }
}


