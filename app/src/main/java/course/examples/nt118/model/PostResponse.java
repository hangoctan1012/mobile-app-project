package course.examples.nt118.model;

import java.util.List;

public class PostResponse {
    private String _id;
    private String userID;
    private String type; // Moment / Rate / Tip / Recipe
    private String caption;
    private List<String> tag;
    private List<String> media;
    private int like;
    private Location location;
    private List<Comment> comment;
    private String createdAt;
    private boolean meLike; // Được thêm từ backend khi query với userID
    
    // Temporary fields để lưu user info (không có trong backend response)
    private String userName;
    private String userAvatar;

    // Location nested class
    public static class Location {
        private String type;
        private List<Double> coordinates;
        private String name;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<Double> getCoordinates() { return coordinates; }
        public void setCoordinates(List<Double> coordinates) { this.coordinates = coordinates; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // Comment nested class
    public static class Comment {
        private String idUser;
        private String content;
        private List<Comment> children;

        public String getIdUser() { return idUser; }
        public void setIdUser(String idUser) { this.idUser = idUser; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public List<Comment> getChildren() { return children; }
        public void setChildren(List<Comment> children) { this.children = children; }
    }

    // Getters
    public String getId() { return _id; }
    public String get_id() { return _id; }
    public String getUserID() { return userID; }
    public String getType() { return type; }
    public String getCaption() { return caption; }
    public List<String> getTag() { return tag; }
    public List<String> getMedia() { return media; }
    public int getLike() { return like; }
    public Location getLocation() { return location; }
    public List<Comment> getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
    public boolean isMeLike() { return meLike; }

    // Setters
    public void set_id(String _id) { this._id = _id; }
    public void setId(String id) { this._id = id; }
    public void setUserID(String userID) { this.userID = userID; }
    public void setType(String type) { this.type = type; }
    public void setCaption(String caption) { this.caption = caption; }
    public void setTag(List<String> tag) { this.tag = tag; }
    public void setMedia(List<String> media) { this.media = media; }
    public void setLike(int like) { this.like = like; }
    public void setLocation(Location location) { this.location = location; }
    public void setComment(List<Comment> comment) { this.comment = comment; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setMeLike(boolean meLike) { this.meLike = meLike; }

    // Helper methods để tương thích với code cũ
    public String getUserId() { return userID; }
    public void setUserId(String userId) { this.userID = userId; }
    public String getContent() { return caption; }
    public void setContent(String content) { this.caption = content; }
    public List<String> getImages() { return media; }
    public void setImages(List<String> images) { this.media = images; }
    public int getLikesCount() { return like; }
    public void setLikesCount(int likesCount) { this.like = likesCount; }
    public int getCommentsCount() { 
        return comment != null ? comment.size() : 0; 
    }
    public void setCommentsCount(int commentsCount) { /* Không cần set */ }
    public int getSharesCount() { return 0; } // Backend không có share
    public void setSharesCount(int sharesCount) { /* Không cần set */ }
    public boolean isLiked() { return meLike; }
    public void setLiked(boolean liked) { this.meLike = liked; }
    public boolean isShared() { return false; } // Backend không có share
    public void setShared(boolean shared) { /* Không cần set */ }
    
    // Helper để lấy userName và userAvatar (sẽ cần load từ User service)
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
}
