import { useState } from "react";
import { useSelector } from "react-redux";
import {
  Form,
  Input,
  Button,
  Upload,
  Radio,
  message,
  Modal,
  Card,
  Typography,
} from "antd";
import { UploadOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const { TextArea } = Input;
const { Title, Text } = Typography;

const UploadPost = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const [fileList, setFileList] = useState([]);
  const [postType, setPostType] = useState("Moment");
  const [open, setOpen] = useState(true);
  const [loading, setLoading] = useState(false);

  // ✅ Lấy userID từ Redux
  const userData = useSelector((state) => state.userData);
  const userID = userData?.id;

  const API_UPLOAD = "http://localhost:3000/api/post/upload"; // ⚠️ backend port 4001

  const handleUploadChange = ({ fileList }) => setFileList(fileList);

  const onFinish = async (values) => {
    if (!userID) {
      message.error("Không tìm thấy người dùng 😢");
      return;
    }

    const formData = new FormData();
    formData.append("userID", userID);
    formData.append("type", postType);
    formData.append("caption", values.caption || "");

    // ✅ tag
    const tagArr =
      values.tag && values.tag.trim() !== ""
        ? values.tag.split(",").map((t) => t.trim())
        : [];
    formData.append("tag", JSON.stringify(tagArr));

    // ✅ location
    const locationObj = {
      type: "Point",
      coordinates: [
        parseFloat(values.longitude) || 106.7,
        parseFloat(values.latitude) || 10.8,
      ],
      name: values.locationName?.trim() || "TP.HCM",
    };
    formData.append("location", JSON.stringify(locationObj));

    // ✅ media
    fileList.forEach((file) => {
      if (file.originFileObj) formData.append("media", file.originFileObj);
    });

    try {
      setLoading(true);
      const res = await axios.post(API_UPLOAD, formData, {
        headers: { "Content-Type": "multipart/form-data" },
        withCredentials: true
      });

      if (res.data?.success) {
        message.success("🎉 Đăng bài thành công!");
        form.resetFields();
        setFileList([]);
        setTimeout(() => {
          setOpen(false);
          navigate("/");
        }, 800);
      } else {
        message.error(res.data?.message || "Đăng bài thất bại 😢");
      }
    } catch (err) {
      console.error("❌ Lỗi gửi form:", err);
      message.error("Không thể kết nối server");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      open={open}
      footer={null}
      onCancel={() => setOpen(false)}
      width={650}
      centered
      destroyOnClose
    >
      <Card
        style={{
          borderRadius: 16,
          boxShadow: "0 6px 20px rgba(0,0,0,0.08)",
          padding: 16,
        }}
      >
        <Title
          level={3}
          style={{ textAlign: "center", marginBottom: 16, color: "#1677ff" }}
        >
          📝 Tạo bài viết mới
        </Title>

        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          initialValues={{
            longitude: 106.7,
            latitude: 10.8,
            locationName: "TP.HCM",
          }}
        >
          {/* 🧩 Loại bài viết */}
          <Form.Item label="Loại bài viết" required>
            <Radio.Group
              onChange={(e) => setPostType(e.target.value)}
              value={postType}
            >
              <Radio.Button value="Moment">Moment</Radio.Button>
              <Radio.Button value="Tip">Tip</Radio.Button>
              <Radio.Button value="Rate">Rate</Radio.Button>
              <Radio.Button value="Recipe">Recipe</Radio.Button>
            </Radio.Group>
          </Form.Item>

          <Form.Item name="caption" label="Nội dung bài viết">
            <TextArea
              rows={4}
              placeholder="Viết gì đó thú vị..."
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Form.Item name="tag" label="Hashtags (ngăn cách bởi dấu phẩy)">
            <Input placeholder="ví dụ: món ăn, mẹo nhỏ, chia sẻ" />
          </Form.Item>

          <Form.Item label="Vị trí">
            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
              <Form.Item name="locationName" noStyle>
                <Input placeholder="Tên địa điểm" />
              </Form.Item>
              <div style={{ display: "flex", gap: "10px" }}>
                <Form.Item name="longitude" noStyle>
                  <Input placeholder="Kinh độ" style={{ flex: 1 }} />
                </Form.Item>
                <Form.Item name="latitude" noStyle>
                  <Input placeholder="Vĩ độ" style={{ flex: 1 }} />
                </Form.Item>
              </div>
            </div>
          </Form.Item>

          {/* 🖼️ Upload ảnh */}
          <Form.Item label="Ảnh bài viết">
            <Upload
              listType="picture-card"
              multiple
              fileList={fileList}
              beforeUpload={() => false}
              onChange={handleUploadChange}
              style={{ width: "100%" }}
            >
              {fileList.length >= 8 ? null : (
                <div>
                  <UploadOutlined />
                  <div style={{ marginTop: 8 }}>Chọn ảnh</div>
                </div>
              )}
            </Upload>
            <Text type="secondary">
              (Có thể chọn nhiều ảnh, tối đa 8 ảnh mỗi bài)
            </Text>
          </Form.Item>

          <Button
            type="primary"
            htmlType="submit"
            size="large"
            block
            loading={loading}
          >
            {loading ? "Đang đăng..." : "Đăng bài"}
          </Button>
        </Form>
      </Card>
    </Modal>
  );
};

export default UploadPost;
