package course.examples.nt118;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import course.examples.nt118.model.UserResponse;
import course.examples.nt118.network.ApiService;
import course.examples.nt118.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ImageView coverImageView, profileImageView, backButton, notificationButton;
    private TextView nameTextView, emailTextView, linkTextView,
            postsCountTextView, followersCountTextView, followingCountTextView;
    private Button editButton;

    private String userId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Khởi tạo RetrofitClient
        RetrofitClient.init(this);

        coverImageView = findViewById(R.id.coverImageView);
        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        linkTextView = findViewById(R.id.linkTextView);
        postsCountTextView = findViewById(R.id.postsCountTextView);
        followersCountTextView = findViewById(R.id.followersCountTextView);
        followingCountTextView = findViewById(R.id.followingCountTextView);
        editButton = findViewById(R.id.editProfileButton);
        backButton = findViewById(R.id.backButton);
        notificationButton = findViewById(R.id.notificationButton);

        apiService = RetrofitClient.getApiService();

        userId = getIntent().getStringExtra("USER_ID");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserProfile(userId);

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());
        notificationButton.setOnClickListener(v ->
                Toast.makeText(ProfileActivity.this, "Notifications clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadUserProfile(String id) {
        Call<UserResponse> call = apiService.getUserById(id);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();

                    nameTextView.setText(user.getName());
                    emailTextView.setText(user.getEmail());
                    linkTextView.setText(user.getLink() != null && !user.getLink().isEmpty() ? user.getLink().get(0) : "-");
                    postsCountTextView.setText(String.valueOf(user.getNumPosts()));
                    followersCountTextView.setText(String.valueOf(user.getNumFollowed()));
                    followingCountTextView.setText(String.valueOf(user.getNumFollowing()));

                    String avatarUrl = user.getAvatar() != null && !user.getAvatar().isEmpty()
                            ? user.getAvatar()
                            : "https://i.pravatar.cc/150?u=" + user.getEmail();

                    String coverUrl = user.getCoverImage() != null && !user.getCoverImage().isEmpty()
                            ? user.getCoverImage()
                            : "https://via.placeholder.com/600x200.png?text=Cover+Image";

                    Glide.with(ProfileActivity.this).load(avatarUrl).into(profileImageView);
                    Glide.with(ProfileActivity.this).load(coverUrl).into(coverImageView);

                } else {
                    Toast.makeText(ProfileActivity.this, "Không tải được thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
