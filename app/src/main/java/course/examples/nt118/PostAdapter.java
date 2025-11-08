package course.examples.nt118;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import course.examples.nt118.model.PostResponse;
import course.examples.nt118.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostResponse> postList;
    private Context context;
    private ApiService apiService;
    private String currentUserID; // User ID của người đang dùng app

    public PostAdapter(List<PostResponse> postList, Context context, ApiService apiService, String currentUserID) {
        this.postList = postList;
        this.context = context;
        this.apiService = apiService;
        this.currentUserID = currentUserID;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostResponse post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarImageView;
        private TextView nameTextView;
        private TextView timeTextView;
        private TextView contentTextView;
        private ImageView postImageView;
        private TextView likesCountTextView;
        private TextView commentsCountTextView;
        private TextView sharesCountTextView;
        private ImageView likeButton;
        private ImageView commentButton;
        private ImageView shareButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            postImageView = itemView.findViewById(R.id.postImageView);
            likesCountTextView = itemView.findViewById(R.id.likesCountTextView);
            commentsCountTextView = itemView.findViewById(R.id.commentsCountTextView);
            sharesCountTextView = itemView.findViewById(R.id.sharesCountTextView);
            likeButton = itemView.findViewById(R.id.likeButton);
            commentButton = itemView.findViewById(R.id.commentButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }

        public void bind(PostResponse post) {
            // Avatar
            String avatarUrl = post.getUserAvatar() != null && !post.getUserAvatar().isEmpty()
                    ? post.getUserAvatar()
                    : "https://i.pravatar.cc/150?u=" + post.getUserID();
            Glide.with(context)
                    .load(avatarUrl)
                    .circleCrop()
                    .into(avatarImageView);

            // Name and time
            String userName = post.getUserName() != null ? post.getUserName() : "Người dùng";
            nameTextView.setText(userName);
            
            // Format thời gian
            String timeText = formatTime(post.getCreatedAt());
            timeTextView.setText(timeText);

            // Content (caption)
            String caption = post.getCaption() != null ? post.getCaption() : "";
            contentTextView.setText(caption);

            // Post image (media) - hiển thị ảnh đầu tiên nếu có
            if (post.getMedia() != null && !post.getMedia().isEmpty() && !post.getMedia().get(0).isEmpty()) {
                postImageView.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(post.getMedia().get(0))
                        .into(postImageView);
            } else {
                postImageView.setVisibility(View.GONE);
            }

            // Counts
            likesCountTextView.setText(formatCount(post.getLike()));
            int commentsCount = post.getComment() != null ? post.getComment().size() : 0;
            commentsCountTextView.setText(formatCount(commentsCount) + " bình luận");
            
            // Ẩn share vì backend không có tính năng này
            sharesCountTextView.setVisibility(View.GONE);

            // Like button state (sử dụng meLike từ backend)
            if (post.isMeLike()) {
                likeButton.setImageResource(android.R.drawable.btn_star_big_on);
                likeButton.setColorFilter(context.getResources().getColor(android.R.color.holo_red_dark));
            } else {
                likeButton.setImageResource(android.R.drawable.btn_star_big_off);
                likeButton.clearColorFilter();
            }

            // Click listeners
            likeButton.setOnClickListener(v -> {
                if (post.isMeLike()) {
                    unlikePost(post);
                } else {
                    likePost(post);
                }
            });

            commentButton.setOnClickListener(v -> 
                Toast.makeText(context, "Tính năng comment đang phát triển", Toast.LENGTH_SHORT).show()
            );

            // Ẩn share button vì backend không có
            shareButton.setVisibility(View.GONE);

            // Click vào avatar hoặc name để xem profile
            avatarImageView.setOnClickListener(v -> {
                // Có thể mở profile của người đăng
                Toast.makeText(context, "Xem profile " + userName, Toast.LENGTH_SHORT).show();
            });

            nameTextView.setOnClickListener(v -> {
                Toast.makeText(context, "Xem profile " + userName, Toast.LENGTH_SHORT).show();
            });
        }

        private String formatTime(String createdAt) {
            if (createdAt == null || createdAt.isEmpty()) {
                return "Vừa xong";
            }
            try {
                // Parse ISO 8601 date string từ MongoDB (có thể có nhiều format)
                java.util.Date date = null;
                
                // Thử các format khác nhau
                String[] formats = {
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                    "yyyy-MM-dd'T'HH:mm:ssXXX"
                };
                
                for (String format : formats) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format, java.util.Locale.getDefault());
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                        date = sdf.parse(createdAt);
                        break;
                    } catch (Exception e) {
                        // Thử format tiếp theo
                    }
                }
                
                if (date == null) {
                    return createdAt; // Nếu không parse được, trả về nguyên bản
                }
                
                long now = System.currentTimeMillis();
                long diff = now - date.getTime();
                
                if (diff < 0) {
                    return "Vừa xong"; // Nếu thời gian trong tương lai
                }
                
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                
                if (days > 0) {
                    return days + " ngày trước";
                } else if (hours > 0) {
                    return hours + " giờ trước";
                } else if (minutes > 0) {
                    return minutes + " phút trước";
                } else {
                    return "Vừa xong";
                }
            } catch (Exception e) {
                return createdAt;
            }
        }

        private void likePost(PostResponse post) {
            if (apiService != null && currentUserID != null && post.getId() != null) {
                Map<String, String> body = new java.util.HashMap<>();
                body.put("userID", currentUserID); // User ID của người đang like
                body.put("postID", post.getId());
                
                apiService.likePost(body).enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, retrofit2.Response<Map<String, String>> response) {
                        if (response.isSuccessful()) {
                            post.setMeLike(true);
                            post.setLike(post.getLike() + 1);
                            notifyItemChanged(getAdapterPosition());
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        // Update UI anyway for better UX
                        post.setMeLike(true);
                        post.setLike(post.getLike() + 1);
                        notifyItemChanged(getAdapterPosition());
                    }
                });
            } else {
                // Update UI anyway
                post.setMeLike(true);
                post.setLike(post.getLike() + 1);
                notifyItemChanged(getAdapterPosition());
            }
        }

        private void unlikePost(PostResponse post) {
            if (apiService != null && currentUserID != null && post.getId() != null) {
                Map<String, String> body = new java.util.HashMap<>();
                body.put("userID", currentUserID); // User ID của người đang unlike
                body.put("postID", post.getId());
                
                apiService.unlikePost(body).enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, retrofit2.Response<Map<String, String>> response) {
                        if (response.isSuccessful()) {
                            post.setMeLike(false);
                            post.setLike(Math.max(0, post.getLike() - 1));
                            notifyItemChanged(getAdapterPosition());
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        post.setMeLike(false);
                        post.setLike(Math.max(0, post.getLike() - 1));
                        notifyItemChanged(getAdapterPosition());
                    }
                });
            } else {
                post.setMeLike(false);
                post.setLike(Math.max(0, post.getLike() - 1));
                notifyItemChanged(getAdapterPosition());
            }
        }

        private void sharePost(PostResponse post) {
            // Backend không có tính năng share
            Toast.makeText(context, "Tính năng chia sẻ chưa được hỗ trợ", Toast.LENGTH_SHORT).show();
        }

        private String formatCount(int count) {
            if (count < 1000) {
                return String.valueOf(count);
            } else if (count < 1000000) {
                return String.format("%.1fK", count / 1000.0);
            } else {
                return String.format("%.1fM", count / 1000000.0);
            }
        }
    }
}

