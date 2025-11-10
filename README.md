# Cookial - Full-Stack Social Media Platform (Ẩm thực)

Dự án mạng xã hội về ẩm thực với kiến trúc microservices, bao gồm:
- **Backend Services**: Node.js + Express + MongoDB
- **Web Frontend**: React + Redux + Vite
- **Mobile App**: Android (Kotlin/Java)
- **API Gateway**: Central routing và authentication

## 📁 Cấu trúc dự án

```
cur/
├── BackEnd_auth/          # Service xác thực (Port 3001)
│   └── BackEnd_auth/
│       ├── models/        # User model (Mongoose)
│       ├── routes/        # Auth routes (login, register, forgot password)
│       └── server.js
│
├── BackEnd_user/          # Service quản lý người dùng (Port 3002)
│   └── BackEnd_user/
│       ├── models/        # User model
│       ├── routes/        # User routes (profile, edit profile)
│       └── server.js
│
├── BackEnd_post/          # Service quản lý bài viết (Port 4001)
│   └── BackEnd_post/
│       ├── models/        # Post, Like, Comment models
│       ├── routes/        # Post routes (upload, like, comment)
│       └── server.js
│
├── FrontEnd/              # Web Application (Port 5173)
│   └── FrontEnd/
│       └── webClient/
│           ├── src/
│           │   ├── components/    # React components
│           │   ├── routes/        # React Router config
│           │   └── store.jsx      # Redux store
│           └── package.json
│
├── gateway/               # API Gateway (Port 3000)
│   └── gateway/
│       ├── routes/        # Route configuration
│       └── server.js      # Gateway server với JWT middleware
│
└── MobileApp/             # Android Application
    ├── app/
    │   └── src/
    │       └── main/
    │           ├── java/  # Java source code
    │           └── res/   # Android resources
    ├── build.gradle.kts
    └── settings.gradle.kts
```

## 🚀 Cách chạy dự án

### 1. Backend Services

#### Cài đặt dependencies:
```bash
# Backend Auth
cd BackEnd_auth/BackEnd_auth
npm install

# Backend User
cd ../../BackEnd_user/BackEnd_user
npm install

# Backend Post
cd ../../BackEnd_post/BackEnd_post
npm install

# Gateway
cd ../../gateway/gateway
npm install
```

#### Chạy các services:
```bash
# Terminal 1: Gateway (Port 3000)
cd gateway/gateway
node server.js

# Terminal 2: Auth Service (Port 3001)
cd BackEnd_auth/BackEnd_auth
node server.js

# Terminal 3: User Service (Port 3002)
cd BackEnd_user/BackEnd_user
node server.js

# Terminal 4: Post Service (Port 4001)
cd BackEnd_post/BackEnd_post
node server.js
```

### 2. Web Frontend

```bash
cd FrontEnd/FrontEnd/webClient
npm install
npm run dev
```

Frontend sẽ chạy tại: `http://localhost:5173`

### 3. Mobile App (Android)

```bash
cd MobileApp
./gradlew build
```

Hoặc mở bằng Android Studio và chạy trực tiếp.

**Lưu ý**: 
- Mobile app kết nối đến Gateway tại `http://10.0.2.2:3000/api/` (Android Emulator)
- Nếu test trên thiết bị thật, đổi IP trong `RetrofitClient.java` thành IP máy tính của bạn

## 🔧 Cấu hình

### Environment Variables

Tạo file `.env` trong mỗi backend service:

**BackEnd_auth/.env:**
```
MyJWT_SECRET=your_jwt_secret_key
MONGO_URI=your_mongodb_connection_string
SMTP_USER=your_email@gmail.com
SMTP_PASS=your_email_password
```

**BackEnd_user/.env:**
```
MONGO_URI=your_mongodb_connection_string
```

**BackEnd_post/.env:**
```
MONGO_URI=your_mongodb_connection_string
```

**gateway/.env:**
```
GATEWAY_PORT=3000
MyJWT_SECRET=your_jwt_secret_key (phải giống với BackEnd_auth)
```

## 📡 API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Đăng ký
- `POST /api/auth/login` - Đăng nhập
- `GET /api/auth/me` - Kiểm tra token
- `POST /api/auth/logout` - Đăng xuất
- `POST /api/auth/forgot-password` - Quên mật khẩu
- `POST /api/auth/verify-otp` - Xác minh OTP
- `POST /api/auth/reset-password` - Đặt lại mật khẩu

### Users (`/api/users`)
- `GET /api/users` - Lấy tất cả users
- `GET /api/users/:id` - Lấy user theo ID
- `POST /api/users/editProfile/:id` - Chỉnh sửa profile

### Posts (`/api/post`)
- `GET /api/post?userID=xxx&after=xxx` - Lấy posts (pagination)
- `POST /api/post/upload` - Upload bài viết
- `POST /api/post/like` - Like bài viết
- `DELETE /api/post/like` - Unlike bài viết

## 🗄️ Database

MongoDB với các collections:
- **users**: Thông tin người dùng
- **posts**: Bài viết (Moment, Rate, Tip, Recipe)
- **likes**: Quan hệ like (composite key: `userID_postID`)

## 🛠️ Công nghệ sử dụng

### Backend
- Node.js + Express.js
- MongoDB + Mongoose
- JWT Authentication
- Bcrypt (password hashing)
- Nodemailer (OTP email)
- Multer (file upload)
- Cloudinary (image storage)

### Web Frontend
- React 19
- Redux Toolkit
- React Router
- Ant Design
- Vite
- Axios
- SCSS

### Mobile App
- Android (Java)
- Retrofit2 (API client)
- OkHttp3 (HTTP client)
- Gson (JSON parsing)
- Glide (Image loading)

## 📝 Tính năng

### Đã triển khai:
- ✅ Đăng ký/Đăng nhập/Đăng xuất
- ✅ Quên mật khẩu (OTP qua email)
- ✅ Quản lý profile
- ✅ Upload bài viết (hỗ trợ nhiều ảnh)
- ✅ Like/Unlike bài viết
- ✅ Comment (nested comments)
- ✅ Newsfeed với pagination
- ✅ Xem profile người khác
- ✅ JWT Authentication
- ✅ API Gateway với proxy

### Tính năng web:
- ✅ Protected routes
- ✅ Redux state management
- ✅ Responsive UI với Ant Design

### Tính năng mobile:
- ✅ Login/Register
- ✅ Profile management
- ✅ Edit profile
- ✅ Cookie-based authentication

## 🔐 Bảo mật

- JWT tokens với HTTP-only cookies
- Bcrypt password hashing
- CORS configuration
- Rate limiting cho OTP
- Token expiration (24h)

## 📱 Platform Support

- **Web**: Chrome, Firefox, Safari, Edge
- **Mobile**: Android 7.0+ (API 24+)

## 👥 Tác giả

Dự án Cookial - Mạng xã hội ẩm thực

## 📄 License

ISC

