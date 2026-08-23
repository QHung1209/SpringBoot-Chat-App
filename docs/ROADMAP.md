# 🚀 Lộ Trình Phát Triển Tính Năng (Feature Roadmap) - MCXX Chat App

Tài liệu này tổng hợp toàn bộ hiện trạng hệ thống và danh sách các tính năng cần phát triển tiếp theo cho backend và frontend của ứng dụng Chat.

---

## 📊 1. Hiện Trạng Hệ Thống (Đã Hoàn Thành)

| Module | Tính năng đã có |
| :--- | :--- |
| **Auth & Security** | Đăng ký, Đăng nhập, JWT Access/Refresh Token, Request Logging Filter. |
| **User & Profile** | Cập nhật thông tin cá nhân, avatar, tìm kiếm người dùng. |
| **User Relation** | Gửi/Chấp nhận/Từ chối lời mời kết bạn, Danh sách bạn bè, Chặn người dùng. |
| **Conversations** | Tạo chat 1-1, Tạo group chat, Cập nhật thông tin nhóm, Quản lý thành viên (Thêm/Xóa). |
| **Messaging** | Gửi tin nhắn Text, Phản hồi (Reply), Thu hồi/Xóa (Delete), Pagination (before/after). |
| **Reactions** | Thả cảm xúc tin nhắn (Like, Love, Haha, Wow, Sad, Angry) + Realtime sync. |
| **Realtime STOMP** | Broadcast tin nhắn mới, Seen status, Typing indicator, Member join/leave, Reactions. |
| **Media & S3** | Upload nhiều ảnh/file/video qua S3 Presigned URL, Xem ảnh bảo mật (Presigned GET URL). |

---

## 🎯 2. Danh Sách Tính Năng Cần Làm Tiếp Theo

### 🔥 Mức Độ Ưu Tiên Cao (P1 - Core Chat Experience)

#### 1. Ghim Tin Nhắn (Pin / Unpin Message)
- **Mục tiêu**: Cho phép ghim các tin nhắn quan trọng lên đầu thanh chat trong cuộc trò chuyện 1-1 hoặc nhóm.
- **Backend cần làm**:
  - `POST /api/v1/messages/{messageId}/pin` - Ghim tin nhắn.
  - `POST /api/v1/messages/{messageId}/unpin` - Bỏ ghim tin nhắn.
  - `GET /api/v1/conversations/{conversationId}/pinned-messages` - Danh sách các tin nhắn đã ghim.
  - Broadcast sự kiện WebSocket: `MessagePinnedEvent` / `MessageUnpinnedEvent`.

#### 2. Chỉnh Sửa Tin Nhắn (Edit Message)
- **Mục tiêu**: Người gửi có thể chỉnh sửa lại nội dung tin nhắn text đã gửi trong một khoảng thời gian nhất định (ví dụ 15-30 phút).
- **Backend cần làm**:
  - `PUT /api/v1/messages/{messageId}` - Cập nhật nội dung tin nhắn (chỉ người gửi mới có quyền, không cho sửa tin nhắn đã xóa).
  - Đánh dấu `is_edited = true` và `edited_at` vào metadata hoặc cột riêng.
  - Broadcast sự kiện WebSocket `MessageEditedEvent`.

#### 3. Tìm Kiếm Tin Nhắn (Search Messages)
- **Mục tiêu**: Tìm kiếm nội dung tin nhắn theo từ khóa.
- **Backend cần làm**:
  - `GET /api/v1/conversations/{conversationId}/messages/search?query=...` - Tìm trong một cuộc trò chuyện cụ thể.
  - `GET /api/v1/messages/search?query=...` - Tìm kiếm tin nhắn trên toàn bộ các cuộc trò chuyện của user.
  - Hỗ trợ phân trang và highlight kết quả.

#### 4. Quản Trị Nhóm Nâng Cao (Group Administration)
- **Mục tiêu**: Hoàn thiện các quyền hạn trong nhóm chat.
- **Backend cần làm**:
  - `POST /api/v1/conversations/{conversationId}/leave` - Tự rời nhóm (nếu là Owner/Admin cuối cùng thì bắt buộc chuyển quyền trước khi rời).
  - `PUT /api/v1/conversations/{conversationId}/members/{userId}/role` - Bổ nhiệm/Hạ quyền Phó nhóm (Admin).
  - `POST /api/v1/conversations/{conversationId}/transfer-owner` - Chuyển quyền Trưởng nhóm (Owner).
  - `DELETE /api/v1/conversations/{conversationId}` - Giải tán nhóm chat (chỉ Owner).

---

### ⚡ Mức Độ Ưu Tiên Trung Bình (P2 - Engagement & Notifications)

#### 5. Push Notification Qua Firebase Cloud Messaging (FCM)
- **Mục tiêu**: Nhận thông báo khi ứng dụng đang đóng hoặc chạy nền trên điện thoại / trình duyệt.
- **Backend cần làm**:
  - Tích hợp `Firebase Admin SDK`.
  - Bắt sự kiện `MessageCreatedEvent`, lọc các thành viên đang offline hoặc không active socket để gửi FCM notification.
  - Hỗ trợ deep-link mở đúng cuộc trò chuyện khi bấm vào notification.

#### 6. Chuyển Tiếp Tin Nhắn (Forward Message)
- **Mục tiêu**: Chuyển tiếp 1 hoặc nhiều tin nhắn (text, media) sang một hoặc nhiều cuộc trò chuyện khác.
- **Backend cần làm**:
  - `POST /api/v1/messages/forward` - Body gồm danh sách `messageIds` và danh sách `targetConversationIds`.
  - Tự động clone nội dung, media liên kết và gắn metadata `forwarded_from`.

#### 7. Trạng Thái Hoạt Động (User Online / Offline Status)
- **Mục tiêu**: Hiển thị chấm xanh online và "Hoạt động x phút trước" (Last seen).
- **Backend cần làm**:
  - Sử dụng Redis để quản lý Heartbeat / Session WebSocket (`SET user:status:{userId} "ONLINE" EX 60`).
  - Lắng nghe sự kiện STOMP `SessionConnectEvent` và `SessionDisconnectEvent`.
  - `GET /api/v1/users/{userId}/status` hoặc batch status cho danh sách bạn bè / thành viên nhóm.
  - Broadcast status change qua socket.

---

### 🌟 Mức Độ Mở Rộng (P3 - Advanced Features)

#### 8. Tin Nhắn Tự Hủy / Tin Nhắn Bí Mật (Disappearing Messages)
- **Mục tiêu**: Tin nhắn tự động biến mất sau một khoảng thời gian thiết lập (ví dụ: 24h, 7 ngày).
- **Cơ chế**: Spring Scheduler / Redis TTL quét và xóa tin nhắn hết hạn.

#### 9. Cuộc Gọi Thoại & Video Call 1-1 (WebRTC Signaling)
- **Mục tiêu**: Hỗ trợ gọi audio / video trực tiếp qua trình duyệt.
- **Backend cần làm**:
  - Server STOMP làm vai trò WebRTC Signaling (trao đổi Offer, Answer, ICE Candidate).
  - Sự kiện: `CallInitiated`, `CallAccepted`, `CallRejected`, `CallEnded`.

---

## 📌 Gợi Ý Thứ Tự Thực Hiện Tiếp Theo

```text
[1. Ghim tin nhắn (Pin/Unpin)] ──▶ [2. Chỉnh sửa tin nhắn (Edit)] ──▶ [3. Tìm kiếm tin nhắn] ──▶ [4. Rời nhóm / Đổi quyền Admin]
```
