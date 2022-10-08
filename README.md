# DỰ ÁN CUSTOMER RELATIONSHIP MANAGEMENT
- Yêu cầu hệ thống
    - Xây dựng hệ thống CRM quản lý công việc nhân viên công ty với các yêu cầu sau:
        - Hệ thống cho phép quản trị hệ thống (ADMIN) đăng nhập và thêm mới, sửa, xóa, xem thông tin, cấp quyền cho nhân viên.
        - Hệ thống đảm bảo cho “quản lý dự án” (LEADER) có thể dễ dàng đăng nhập thêm mới, sửa, xóa, xem thông tin dự án. Đồng thời có thể thêm nhân viên vào dự án và phân công công việc cho từng nhân viên thuộc dự án.
        - Hệ thống cũng cho phép “quản lý dự án” có thể theo dõi các thống kê về tiến độ công việc của từng nhân viên trong mỗi dự án.
        - Hệ thống cho phép nhân viên đăng nhập với tư cách thành viên, cập nhật tiến độ công việc. Xem các thống kê về tiến độ của các việc đã và đang thực hiện.
        - Hệ thống cũng cho phép quản trị hệ thống xem các thống kê về tiến độ các dự án.
- Phân tích hệ thống
    - Loại người dùng (quyền):
        - Quản trị hệ thống (Admin): Người dùng có toàn quyền đối với hệ thống, có thể thêm, sửa, xóa, xem thông tin và cấp quyền cho các thành viên khác, xem thống kê về tất cả các dự án.
        - Quản lý dự án (Leader): Xem danh sách nhân viên của công ty, thêm, sửa, xóa, xem thông tin dự án do mình quản lý, ngoài ra có thể thêm hoặc loại bỏ người dùng là nhân viên thường vào dự án, phân công công việc và xem thống kê về dự án thuộc về mình.
        - Nhân viên (Member): Người dùng chỉ có quyền xem các công việc được giao trong mỗi dự án, cập nhật tiến độ công việc được giao, xem thống kê tiến độ các dự án của bản thân.
    - Các module:
        - Quản lý quyền: Các thông tin như tên quyền, mô tả.
        - Quản lý người dùng: Các thông tin như email, mật khẩu, họ tên, địa chỉ, số điện thoại, loại thành viên (quyền).
        - Quản lý dự án: Các thông tin như tên dự án, mô tả, ngày bắt đầu, ngày kết thúc, mã người dùng (người tạo dự án).
        - Quản lý công việc: Các thông tin như tên công việc, mô tả, ngày bắt đầu, ngày kết thúc, mã người dùng(người thực hiện), mã dự án (thuộc dự án nào), mã trạng thái (chưa bắt đầu, đang thực hiện, đã hoàn thành).
- Use Case
    - Quản trị viên (admin):
        - Quản lý tất cả thành viên
        - Quản lý tất cả nhóm việc
        - Quản lý quyền người dùng
        - Quản lý tất cả các dự án
        - Đăng nhập hệ thống
        - Quản lý tài khoản cá nhân
    - Quản lý dự án (leader):
        - Quản lý dự án
        - Quản lý thành viên dự án
        - Xem danh sách tất cả nhân viên
        - Đăng nhập hệ thống
        - Quản lý tài khoản cá nhân
    - Thành viên (member)
        - Đăng nhập hệ thống
        - Cập nhật tiến độ công việc
        - Xem thống kê công việc (cá nhân)
        - Quản lý tài khoản cá nhân

## LINK DEMO
<div align="center">

[Click vào đây để xem chi tiết](https://crm-project.herokuapp.com)

</div>

## HÌNH ẢNH DEMO
<p align="center">
<img src="https://raw.githubusercontent.com/Tynab/CRM-Project/master/pic/0.jpg"></img>
</p>

## VIDEO DEMO
<div align="center">

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/3W1kdSJdITo/0.jpg)](https://youtu.be/3W1kdSJdITo)

</div>

## CẤU HÌNH API REFRESH TOKEN
```java
// Refresh token
    @GetMapping(TOKEN_VIEW + REFRESH_VIEW)
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws StreamWriteException, DatabindException, IOException {
        var header = request.getHeader(AUTHORIZATION);
        // check token format in authorization header
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            // get token from authorization header
            try {
                var refreshToken = header.substring(TOKEN_PREFIX.length());
                var algorithm = HMAC256(SECRET_KEY.getBytes());
                var user = userService.getUser(require(algorithm).build().verify(refreshToken).getSubject());
                var tokens = new HashMap<>();
                tokens.put(ACCESS_TOKEN_KEY,
                        create().withSubject(user.getEmail())
                                .withExpiresAt(new Date(currentTimeMillis() + EXPIRATION_TIME))
                                .withIssuer(request.getRequestURL().toString())
                                .withClaim(ROLE_CLAIM_KEY,
                                        singleton(new Role(ROLE_PREFIX + user.getRole().getName().toUpperCase()))
                                                .stream().map(Role::getName).collect(toList()))
                                .sign(algorithm));
                tokens.put(REFRESH_TOKEN_KEY, refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                var errorMsg = e.getMessage();
                response.setHeader(ERROR_HEADER_KEY, errorMsg);
                response.setStatus(FORBIDDEN.value());
                var error = new HashMap<>();
                error.put(ERROR_MESSAGE_KEY, errorMsg);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
```

## EER Diagram
<p align="center">
<img src="https://raw.githubusercontent.com/Tynab/CRM-Project/master/design/EER%20Diagram.jpg"></img>
</p>

### TÍCH HỢP
<img src="https://raw.githubusercontent.com/Tynab/CRM-Project/main/pic/1.jpg" align="left" width="3%" height="3%"></img>
<div style="display:flex;">

- Java JWT » 4.0.0

</div>
<img src="https://raw.githubusercontent.com/Tynab/CRM-Project/main/pic/2.jpg" align="left" width="3%" height="3%"></img>
<div style="display:flex;">

- Imgscalr A Java Image Scaling Library » 4.2

</div>