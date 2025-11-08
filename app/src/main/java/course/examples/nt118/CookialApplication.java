package course.examples.nt118;

import android.app.Application;
import android.util.Log;

import course.examples.nt118.network.RetrofitClient;

/**
 * Application class để khởi tạo các components chung của app
 * Được khai báo trong AndroidManifest.xml
 */
public class CookialApplication extends Application {

    private static final String TAG = "CookialApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            // Khởi tạo RetrofitClient một lần duy nhất khi app khởi động
            RetrofitClient.init(this);
            Log.d(TAG, "RetrofitClient đã được khởi tạo thành công");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo RetrofitClient trong Application: " + e.getMessage(), e);
            e.printStackTrace();
            // Không throw exception để app vẫn có thể chạy
            // RetrofitClient sẽ được khởi tạo lại trong LoginActivity nếu cần
        }
    }
}

