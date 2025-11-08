package course.examples.nt118.model;

import java.util.List;
import java.util.Map;

public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String avatar;
    private String coverImage;
    private int numPosts;
    private int numFollowed;
    private int numFollowing;
    private List<String> tags;
    private List<String> link;
    private Map<String, List<String>> preference;

    // THÊM token để lưu JWT nếu backend trả
    private String token;

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAvatar() { return avatar; }
    public String getCoverImage() { return coverImage; }
    public int getNumPosts() { return numPosts; }
    public int getNumFollowed() { return numFollowed; }
    public int getNumFollowing() { return numFollowing; }
    public List<String> getTags() { return tags; }
    public List<String> getLink() { return link; }
    public Map<String, List<String>> getPreference() { return preference; }
    public String getToken() { return token; }

    // Setters (nếu cần)
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public void setNumPosts(int numPosts) { this.numPosts = numPosts; }
    public void setNumFollowed(int numFollowed) { this.numFollowed = numFollowed; }
    public void setNumFollowing(int numFollowing) { this.numFollowing = numFollowing; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setLink(List<String> link) { this.link = link; }
    public void setPreference(Map<String, List<String>> preference) { this.preference = preference; }
    public void setToken(String token) { this.token = token; }
}
