package course.examples.nt118;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import course.examples.nt118.model.LoginResponse;
import course.examples.nt118.model.UserResponse;
import course.examples.nt118.network.ApiService;
import course.examples.nt118.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView signInTextView;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo RetrofitClient
        RetrofitClient.init(this);

        nameEditText = findViewById(R.id.editTextName);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.buttonRegister);
        signInTextView = findViewById(R.id.signInTextView);

        apiService = RetrofitClient.getApiService();

        registerButton.setOnClickListener(v -> registerUser());

        signInTextView.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạm thời avatar mặc định
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("password", password);
        body.put("avatar", "https://i.pravatar.cc/150?img=3");

        apiService.registerUser(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    // Đăng ký thành công, tự động đăng nhập
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Đang đăng nhập...", Toast.LENGTH_SHORT).show();
                    
                    // Tự động đăng nhập với thông tin vừa đăng ký
                    Map<String, String> loginBody = new HashMap<>();
                    loginBody.put("email", email);
                    loginBody.put("password", password);
                    
                    apiService.loginUser(loginBody).enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                LoginResponse loginResponse = response.body();
                                UserResponse user = loginResponse.getUser();
                                
                                if (user != null) {
                                    // Lưu cookie vào SharedPreferences
                                    RetrofitClient.saveCookies();
                                    
                                    String userId = user.getId();
                                    if (userId != null && !userId.isEmpty()) {
                                        Toast.makeText(RegisterActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        
                                        // Chuyển sang HomeActivity (Feed Screen)
                                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                        intent.putExtra("USER_ID", userId);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Nếu không lấy được userId, chuyển về LoginActivity
                                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                } else {
                                    // Nếu không có thông tin user, chuyển về LoginActivity
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                }
                            } else {
                                // Đăng nhập thất bại, chuyển về LoginActivity
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            // Lỗi đăng nhập, chuyển về LoginActivity
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                } else {
                    String errorMessage = "Đăng ký thất bại";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("email") || errorBody.contains("Email")) {
                                errorMessage = "Email đã tồn tại";
                            }
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
