package course.examples.nt118.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import course.examples.nt118.config.ApiConfig;
import course.examples.nt118.utils.TokenManager;

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    private static CookieManager cookieManager;
    private static SharedPreferences prefs;

    public static void init(Context context) {
        // Chỉ khởi tạo một lần (idempotent)
        if (retrofit != null && apiService != null) {
            Log.d(TAG, "RetrofitClient đã được khởi tạo trước đó, bỏ qua");
            return;
        }

        try {
            prefs = context.getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);

            String baseUrl = ApiConfig.getBaseUrl();
            Log.d(TAG, "Đang khởi tạo RetrofitClient với baseUrl: " + baseUrl);

            cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            // Nếu có cookie đã lưu, load vào CookieManager
            String cookieStr = prefs.getString("COOKIE", null);
            if (cookieStr != null) {
                try {
                    for (String c : cookieStr.split(";")) {
                        if (!c.trim().isEmpty()) {
                            List<HttpCookie> cookies = HttpCookie.parse(c);
                            if (!cookies.isEmpty()) {
                                cookieManager.getCookieStore().add(URI.create(baseUrl), cookies.get(0));
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Lỗi khi parse cookie: " + e.getMessage());
                    // Ignore cookie parsing errors
                }
            }

            // Logging interceptor (chỉ log trong debug mode)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // ✅ Interceptor tự động gắn token vào Authorization header
            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                String token = TokenManager.getToken(context);
                Request.Builder requestBuilder = original.newBuilder();

                if (token != null && !token.isEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer " + token);
                }

                return chain.proceed(requestBuilder.build());
            };

            // OkHttpClient với timeout và interceptors
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .addInterceptor(logging)
                    .addInterceptor(authInterceptor)
                    .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
            Log.d(TAG, "RetrofitClient đã được khởi tạo thành công");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo RetrofitClient: " + e.getMessage(), e);
            e.printStackTrace();
            throw e; // Throw lại để caller biết có lỗi
        }
    }

    public static ApiService getApiService() {
        return apiService;
    }

    // Ghi cookie vào SharedPreferences sau khi login
    public static void saveCookies() {
        if (cookieManager != null && prefs != null) {
            try {
                List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                StringBuilder sb = new StringBuilder();
                for (HttpCookie cookie : cookies) {
                    sb.append(cookie.toString()).append(";");
                }
                prefs.edit().putString("COOKIE", sb.toString()).apply();
            } catch (Exception e) {
                // Ignore cookie saving errors
            }
        }
    }

    // Xóa cookies khi logout
    public static void clearCookies() {
        if (cookieManager != null) {
            try {
                cookieManager.getCookieStore().removeAll();
            } catch (Exception e) {
                // Ignore
            }
        }
        if (prefs != null) {
            prefs.edit().remove("COOKIE").apply();
        }
    }
}
