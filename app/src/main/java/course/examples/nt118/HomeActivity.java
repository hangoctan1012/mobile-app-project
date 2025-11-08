package course.examples.nt118;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import course.examples.nt118.model.PostResponse;
import course.examples.nt118.model.PostsResponse;
import course.examples.nt118.model.UserResponse;
import course.examples.nt118.network.ApiService;
import course.examples.nt118.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<PostResponse> postList;
    private ApiService apiService;
    
    private ImageView profileImageView;
    private ImageView createPostAvatarImageView;
    private ImageView searchImageView;
    private ImageView notificationImageView;
    private TextView createPostTextView;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_home);
            Log.d(TAG, "Layout đã được load thành công");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi load layout: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
            return;
        }

        // Lấy userId từ intent
        currentUserId = getIntent().getStringExtra("USER_ID");
        Log.d(TAG, "User ID từ intent: " + currentUserId);
        
        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.w(TAG, "Không có USER_ID trong intent, chuyển về LoginActivity");
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        try {
            // Khởi tạo RetrofitClient
            RetrofitClient.init(this);
            apiService = RetrofitClient.getApiService();
            
            if (apiService == null) {
                Log.e(TAG, "ApiService là null sau khi init");
                Toast.makeText(this, "Lỗi khởi tạo kết nối API", Toast.LENGTH_SHORT).show();
            }

            initializeViews();
            setupRecyclerView();
            loadPosts();
            loadCurrentUser();
            
            Log.d(TAG, "HomeActivity đã được khởi tạo thành công");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo HomeActivity: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        try {
            postsRecyclerView = findViewById(R.id.postsRecyclerView);
            profileImageView = findViewById(R.id.profileImageView);
            createPostAvatarImageView = findViewById(R.id.createPostAvatarImageView);
            searchImageView = findViewById(R.id.searchImageView);
            notificationImageView = findViewById(R.id.notificationImageView);
            createPostTextView = findViewById(R.id.createPostTextView);

            // Kiểm tra xem các view có được tìm thấy không
            if (postsRecyclerView == null || profileImageView == null || createPostAvatarImageView == null) {
                Log.e(TAG, "Không tìm thấy một hoặc nhiều view trong layout!");
                Toast.makeText(this, "Lỗi khởi tạo giao diện", Toast.LENGTH_SHORT).show();
                return;
            }

            // Click vào profile
            if (profileImageView != null) {
                profileImageView.setOnClickListener(v -> {
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    intent.putExtra("USER_ID", currentUserId);
                    startActivity(intent);
                });
            }

            // Click vào search
            if (searchImageView != null) {
                searchImageView.setOnClickListener(v -> 
                    Toast.makeText(this, "Tính năng tìm kiếm đang phát triển", Toast.LENGTH_SHORT).show()
                );
            }

            // Click vào notification
            if (notificationImageView != null) {
                notificationImageView.setOnClickListener(v -> 
                    Toast.makeText(this, "Thông báo", Toast.LENGTH_SHORT).show()
                );
            }

            // Click vào create post
            if (createPostTextView != null) {
                createPostTextView.setOnClickListener(v -> 
                    Toast.makeText(this, "Tính năng tạo bài viết đang phát triển", Toast.LENGTH_SHORT).show()
                );
            }
            
            Log.d(TAG, "Views đã được khởi tạo thành công");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo views: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        try {
            postList = new ArrayList<>();
            postAdapter = new PostAdapter(postList, this, apiService, currentUserId);
            postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            postsRecyclerView.setAdapter(postAdapter);
            Log.d(TAG, "RecyclerView đã được setup thành công");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi setup RecyclerView: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void loadCurrentUser() {
        if (currentUserId != null && !currentUserId.isEmpty()) {
            // Nếu là demo user, set avatar mặc định
            if (currentUserId.startsWith("demo_user")) {
                String defaultAvatar = "https://i.pravatar.cc/150?u=demo";
                Glide.with(HomeActivity.this)
                        .load(defaultAvatar)
                        .circleCrop()
                        .into(profileImageView);
                Glide.with(HomeActivity.this)
                        .load(defaultAvatar)
                        .circleCrop()
                        .into(createPostAvatarImageView);
                Log.d(TAG, "Đang sử dụng demo mode");
                return;
            }
            
            if (apiService != null) {
                apiService.getUserById(currentUserId).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserResponse user = response.body();
                            String avatarUrl = user.getAvatar() != null && !user.getAvatar().isEmpty()
                                    ? user.getAvatar()
                                    : "https://i.pravatar.cc/150?u=" + user.getEmail();
                            Glide.with(HomeActivity.this)
                                    .load(avatarUrl)
                                    .circleCrop()
                                    .into(profileImageView);
                            // Cũng set avatar cho create post area
                            Glide.with(HomeActivity.this)
                                    .load(avatarUrl)
                                    .circleCrop()
                                    .into(createPostAvatarImageView);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi khi load user: " + t.getMessage());
                        // Nếu lỗi, set avatar mặc định
                        String defaultAvatar = "https://i.pravatar.cc/150?u=default";
                        Glide.with(HomeActivity.this)
                                .load(defaultAvatar)
                                .circleCrop()
                                .into(profileImageView);
                        Glide.with(HomeActivity.this)
                                .load(defaultAvatar)
                                .circleCrop()
                                .into(createPostAvatarImageView);
                    }
                });
            }
        }
    }

    private void loadPosts() {
        // Nếu là demo mode, tạo posts mẫu ngay
        if (currentUserId != null && currentUserId.startsWith("demo_user")) {
            Log.d(TAG, "Demo mode - Tạo posts mẫu");
            createSamplePosts();
            return;
        }
        
        if (apiService == null || currentUserId == null || currentUserId.isEmpty()) {
            Log.e(TAG, "ApiService hoặc userID chưa được khởi tạo, tạo posts mẫu");
            createSamplePosts();
            return;
        }

        // Gọi API với userID (bắt buộc) và after (cursor, null cho lần đầu)
        apiService.getAllPosts(currentUserId, null).enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PostsResponse postsResponse = response.body();
                    if (postsResponse.isSuccess() && postsResponse.getPosts() != null && !postsResponse.getPosts().isEmpty()) {
                        postList.clear();
                        List<PostResponse> posts = postsResponse.getPosts();
                        
                        // Load thông tin user cho mỗi post
                        loadUserInfoForPosts(posts);
                        
                        postList.addAll(posts);
                        postAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Đã load " + postList.size() + " posts từ API");
                    } else {
                        Log.e(TAG, "Response không thành công hoặc rỗng, tạo posts mẫu");
                        createSamplePosts();
                    }
                } else {
                    Log.e(TAG, "Lỗi khi load posts: " + response.code() + ", tạo posts mẫu");
                    createSamplePosts();
                }
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi load posts: " + t.getMessage() + ", tạo posts mẫu");
                createSamplePosts();
            }
        });
    }

    private void loadUserInfoForPosts(List<PostResponse> posts) {
        // Load thông tin user cho mỗi post (name, avatar)
        // Tối ưu: có thể cache user info để tránh gọi nhiều lần
        for (PostResponse post : posts) {
            if (post.getUserID() != null && !post.getUserID().isEmpty()) {
                apiService.getUserById(post.getUserID()).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserResponse user = response.body();
                            post.setUserName(user.getName());
                            post.setUserAvatar(user.getAvatar());
                            postAdapter.notifyItemChanged(postList.indexOf(post));
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Log.w(TAG, "Không load được user info cho post: " + post.getId());
                    }
                });
            }
        }
    }

    private void createSamplePosts() {
        // Tạo một số posts mẫu để test giao diện (format phù hợp với backend)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(tz);
        
        PostResponse post1 = new PostResponse();
        post1.set_id("1");
        post1.setUserID("1");
        post1.setType("Moment");
        post1.setCaption("Đây là một bài viết mẫu. Giao diện home sẽ hiển thị các bài viết từ bạn bè và những người bạn theo dõi!");
        post1.setUserName("Nguyễn Văn A");
        post1.setUserAvatar("https://i.pravatar.cc/150?img=1");
        java.util.ArrayList<String> media1 = new java.util.ArrayList<>();
        media1.add("https://picsum.photos/400/300?random=1");
        post1.setMedia(media1);
        post1.setLike(25);
        post1.setMeLike(false);
        post1.setCreatedAt(sdf.format(new java.util.Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)));

        PostResponse post2 = new PostResponse();
        post2.set_id("2");
        post2.setUserID("2");
        post2.setType("Recipe");
        post2.setCaption("Chào mừng bạn đến với ứng dụng mạng xã hội của chúng tôi! 🎉");
        post2.setUserName("Trần Thị B");
        post2.setUserAvatar("https://i.pravatar.cc/150?img=2");
        java.util.ArrayList<String> media2 = new java.util.ArrayList<>();
        media2.add("https://picsum.photos/400/300?random=2");
        post2.setMedia(media2);
        post2.setLike(42);
        post2.setMeLike(true);
        post2.setCreatedAt(sdf.format(new java.util.Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000)));

        postList.add(post1);
        postList.add(post2);
        postAdapter.notifyDataSetChanged();
    }
}

