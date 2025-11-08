package course.examples.nt118;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import course.examples.nt118.model.UserResponse;
import course.examples.nt118.network.RetrofitClient;
import course.examples.nt118.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView avatarImageView, backButton;
    private EditText nameEditText, emailEditText, linkEditText;
    private Button saveButton;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Khởi tạo RetrofitClient
        RetrofitClient.init(this);

        avatarImageView = findViewById(R.id.avatarImageView);
        backButton = findViewById(R.id.backButton);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        linkEditText = findViewById(R.id.linkEditText);
        saveButton = findViewById(R.id.saveButton);

        userId = getIntent().getStringExtra("USER_ID");

        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserInfo();

        saveButton.setOnClickListener(v -> updateUserProfile());
        backButton.setOnClickListener(v -> finish());
    }

    private void loadUserInfo() {
        ApiService api = RetrofitClient.getApiService(); // ✅ sửa ở đây

        Call<UserResponse> call = api.getUserById(userId);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();

                    nameEditText.setText(user.getName());
                    emailEditText.setText(user.getEmail());

                    if (user.getLink() != null && !user.getLink().isEmpty()) {
                        linkEditText.setText(String.join("\n", user.getLink()));
                    }

                    Glide.with(EditProfileActivity.this)
                            .load(user.getAvatar())
                            .into(avatarImageView);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không tải được dữ liệu user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        ApiService api = RetrofitClient.getApiService(); // ✅ sửa ở đây

        Map<String, Object> body = new HashMap<>();
        body.put("name", nameEditText.getText().toString());
        body.put("email", emailEditText.getText().toString());
        body.put("avatar", "https://api.dicebear.com/7.x/avataaars/svg?seed=" + emailEditText.getText().toString());
        body.put("link", linkEditText.getText().toString());
        body.put("tags", new String[]{"Food Reviewer"});

        Call<UserResponse> call = api.editProfile(userId, body);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.putExtra("UPDATED", true);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không thể cập nhật", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
