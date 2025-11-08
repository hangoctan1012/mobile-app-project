package course.examples.nt118.config;

/**
 * Cấu hình API cho ứng dụng
 * Thay đổi BASE_URL tùy theo môi trường:
 * - Android Emulator: http://10.0.2.2:3000/api/
 * - Thiết bị thật: http://YOUR_COMPUTER_IP:3000/api/
 * - Production: https://your-domain.com/api/
 */
public class ApiConfig {
    
    // ========== CẤU HÌNH API BASE URL ==========
    // Đổi IP này thành IP máy tính của bạn khi test trên thiết bị thật
    // Để lấy IP: Windows (ipconfig) | Mac/Linux (ifconfig)
    private static final String EMULATOR_BASE_URL = "http://10.0.2.2:3000/api/";
    private static final String DEVICE_BASE_URL = "http://192.168.1.100:3000/api/"; // Thay IP này
    
    // Chọn môi trường: true = Emulator, false = Device/Production
    private static final boolean USE_EMULATOR = true;
    
    // Production URL (nếu có)
    private static final String PRODUCTION_BASE_URL = "https://your-api-domain.com/api/";
    
    // ========== GETTER ==========
    public static String getBaseUrl() {
        if (USE_EMULATOR) {
            return EMULATOR_BASE_URL;
        } else {
            // Có thể kiểm tra BuildConfig.DEBUG để tự động chuyển đổi
            return DEVICE_BASE_URL;
        }
    }
    
    // ========== TIMEOUT SETTINGS ==========
    public static final int CONNECT_TIMEOUT = 30; // seconds
    public static final int READ_TIMEOUT = 30; // seconds
    public static final int WRITE_TIMEOUT = 30; // seconds
}

