package course.examples.nt118;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import course.examples.nt118.model.LoginResponse;
import course.examples.nt118.model.UserResponse;
import course.examples.nt118.network.RetrofitClient;
import course.examples.nt118.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_login);
            Log.d(TAG, "Layout đã được load thành công");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi load layout: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }

        // RetrofitClient đã được khởi tạo trong CookialApplication, không cần gọi lại
        // Chỉ đảm bảo nó đã được init
        try {
            if (RetrofitClient.getApiService() == null) {
                Log.w(TAG, "RetrofitClient chưa được khởi tạo, đang khởi tạo...");
                RetrofitClient.init(this);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo RetrofitClient: " + e.getMessage(), e);
            // Không dừng app, chỉ log lỗi
        }

        try {
            emailEditText = findViewById(R.id.emailEditText);
            passwordEditText = findViewById(R.id.passwordEditText);
            loginButton = findViewById(R.id.signInButton);
            signupTextView = findViewById(R.id.signUpTextView);

            // Kiểm tra xem các view có được tìm thấy không
            if (emailEditText == null || passwordEditText == null || loginButton == null) {
                Log.e(TAG, "Không tìm thấy một hoặc nhiều view trong layout!");
                Log.e(TAG, "emailEditText: " + (emailEditText != null ? "OK" : "NULL"));
                Log.e(TAG, "passwordEditText: " + (passwordEditText != null ? "OK" : "NULL"));
                Log.e(TAG, "loginButton: " + (loginButton != null ? "OK" : "NULL"));
                Toast.makeText(this, "Lỗi khởi tạo giao diện: Không tìm thấy các thành phần", Toast.LENGTH_LONG).show();
                return;
            }

            loginButton.setOnClickListener(v -> loginUser());
        
        // Tạm thời: Thêm nút để test HomeActivity mà không cần server
        // TODO: Xóa sau khi server đã chạy
        loginButton.setOnLongClickListener(v -> {
            Log.d(TAG, "Long click - Chuyển đến HomeActivity với demo mode");
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("USER_ID", "demo_user_123"); // Demo user ID
            startActivity(intent);
            return true;
        });

            if (signupTextView != null) {
                signupTextView.setOnClickListener(v ->
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
                );
            }
            
            Log.d(TAG, "LoginActivity đã được khởi tạo thành công");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo views: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng email cơ bản
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem ApiService đã được khởi tạo chưa
        ApiService apiService = RetrofitClient.getApiService();
        if (apiService == null) {
            Log.e(TAG, "ApiService chưa được khởi tạo!");
            Toast.makeText(this, "Lỗi: Chưa khởi tạo kết nối API. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            // Thử khởi tạo lại
            RetrofitClient.init(this);
            apiService = RetrofitClient.getApiService();
            if (apiService == null) {
                return;
            }
        }

        // Vô hiệu hóa nút đăng nhập để tránh click nhiều lần
        loginButton.setEnabled(false);
        loginButton.setText("Đang đăng nhập...");

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        Log.d(TAG, "Đang gửi request đăng nhập với email: " + email);

        apiService.loginUser(body).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Bật lại nút đăng nhập
                loginButton.setEnabled(true);
                loginButton.setText("Sign in");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    UserResponse user = loginResponse.getUser();
                    
                    if (user != null) {
                        // Lưu cookie vào SharedPreferences
                        RetrofitClient.saveCookies();

                        String userId = user.getId();
                        Log.d(TAG, "Đăng nhập thành công! User ID: " + userId);
                        
                        if (userId == null || userId.isEmpty()) {
                            Log.e(TAG, "User ID là null hoặc rỗng!");
                            Toast.makeText(LoginActivity.this, "Lỗi: Không có User ID", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        
                        try {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("USER_ID", userId);
                            Log.d(TAG, "Đang chuyển đến HomeActivity với USER_ID: " + userId);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi chuyển đến HomeActivity: " + e.getMessage(), e);
                            Toast.makeText(LoginActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    } else {
                        Log.w(TAG, "Response thành công nhưng không có thông tin user");
                        Toast.makeText(LoginActivity.this, "Không nhận được thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý lỗi từ server
                    String errorMessage = "Đăng nhập thất bại";
                    int statusCode = response.code();
                    
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Lỗi từ server (Code: " + statusCode + "): " + errorBody);
                            
                            // Cố gắng parse thông báo lỗi từ server
                            if (errorBody.contains("email") || errorBody.contains("Email")) {
                                errorMessage = "Email hoặc mật khẩu không đúng";
                            } else if (errorBody.contains("password") || errorBody.contains("Password")) {
                                errorMessage = "Mật khẩu không đúng";
                            } else if (statusCode == 401) {
                                errorMessage = "Email hoặc mật khẩu không đúng";
                            } else if (statusCode == 404) {
                                errorMessage = "Không tìm thấy tài khoản";
                            } else if (statusCode == 500) {
                                errorMessage = "Lỗi server. Vui lòng thử lại sau";
                            }
                        } else {
                            Log.e(TAG, "Lỗi từ server (Code: " + statusCode + "): Response body rỗng");
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi khi đọc error body", e);
                    }
                    
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Bật lại nút đăng nhập
                loginButton.setEnabled(true);
                loginButton.setText("Sign in");

                Log.e(TAG, "Lỗi kết nối khi đăng nhập", t);
                
                String errorMessage = "Lỗi kết nối";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("Failed to connect") || t.getMessage().contains("Unable to resolve host")) {
                        errorMessage = "Không thể kết nối đến server. Vui lòng kiểm tra:\n" +
                                "1. Server API đã chạy chưa?\n" +
                                "2. Địa chỉ IP trong ApiConfig có đúng không?\n" +
                                "3. Thiết bị và máy tính có cùng mạng không?";
                    } else if (t.getMessage().contains("timeout")) {
                        errorMessage = "Kết nối quá lâu. Vui lòng thử lại";
                    } else {
                        errorMessage = "Lỗi: " + t.getMessage();
                    }
                }
                
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
