-- Tom tat: Script khoi tao database CRM gom role, user, du an, trang thai
-- cong viec va du lieu mau de demo ung dung Spring Boot.

-- Xoa database cu de script co the chay lai tu dau.

DROP DATABASE IF EXISTS crm;

-- Tao database crm neu chua ton tai.

CREATE DATABASE IF NOT EXISTS crm;

-- Chon database lam viec.

USE crm;

-- Dam bao AUTO_INCREMENT tang tung don vi trong moi lan seed.
SET @@auto_increment_increment=1;

-- Bang vai tro nguoi dung: Admin, Leader, Member.

CREATE TABLE
    IF NOT EXISTS loai_thanh_vien (
        id INT NOT NULL AUTO_INCREMENT,
        ten NVARCHAR(10) NOT NULL,
        mo_ta NVARCHAR(255),
        PRIMARY KEY (id)
    );

-- Bang tai khoan nguoi dung va khoa ngoai den vai tro.

CREATE TABLE
    IF NOT EXISTS nguoi_dung (
        id INT NOT NULL AUTO_INCREMENT,
        email NVARCHAR(50) NOT NULL,
        mat_khau NVARCHAR(255) NOT NULL,
        ho_ten NVARCHAR(50) NOT NULL,
        hinh_anh NVARCHAR(10) NOT NULL,
        dia_chi NVARCHAR(255),
        so_dien_thoai NVARCHAR(20),
        id_loai_thanh_vien INT NOT NULL,
        PRIMARY KEY (id),
        FOREIGN KEY (id_loai_thanh_vien) REFERENCES loai_thanh_vien(id)
    );

-- Bang du an, moi du an co nguoi tao/originator.

CREATE TABLE
    IF NOT EXISTS du_an (
        id INT NOT NULL AUTO_INCREMENT,
        ten NVARCHAR(50) NOT NULL,
        mo_ta NVARCHAR(255),
        ngay_bat_dau DATE NOT NULL,
        ngay_ket_thuc DATE NOT NULL,
        id_nguoi_tao INT NOT NULL,
        PRIMARY KEY (id),
        FOREIGN KEY (id_nguoi_tao) REFERENCES nguoi_dung(id)
    );

-- Bang danh muc trang thai cong viec.

CREATE TABLE
    IF NOT EXISTS trang_thai_cong_viec (
        id INT NOT NULL AUTO_INCREMENT,
        ten NVARCHAR(20) NOT NULL,
        PRIMARY KEY (id)
    );

-- Bang cong viec, lien ket user thuc hien, du an va trang thai.

CREATE TABLE
    IF NOT EXISTS cong_viec (
        id INT NOT NULL AUTO_INCREMENT,
        ten NVARCHAR(50) NOT NULL,
        mo_ta NVARCHAR(255),
        ngay_bat_dau DATE NOT NULL,
        ngay_ket_thuc DATE NOT NULL,
        id_nguoi_thuc_hien INT NOT NULL,
        id_du_an INT NOT NULL,
        id_trang_thai_cong_viec INT NOT NULL,
        PRIMARY KEY (id),
        FOREIGN KEY (id_nguoi_thuc_hien) REFERENCES nguoi_dung(id),
        FOREIGN KEY (id_du_an) REFERENCES du_an(id),
        FOREIGN KEY (id_trang_thai_cong_viec) REFERENCES trang_thai_cong_viec(id)
    );

-- Seed vai tro mac dinh.

INSERT INTO
    loai_thanh_vien (ten, mo_ta)
VALUES ('Admin', 'Quản trị viên'), ('Leader', 'Quản lý dự án'), ('Member', 'Thành viên');

-- Seed user mau; comment sau hash la mat khau plaintext dung cho demo local.

INSERT INTO
    nguoi_dung (
        email,
        mat_khau,
        ho_ten,
        hinh_anh,
        dia_chi,
        so_dien_thoai,
        id_loai_thanh_vien
    )
VALUES (
        'nguyenvana@gmail.com',
        '$2a$12$e/LWlqzxwe58ohgTil5tC.ZyRGEyqHoibrxCc/ihmxYxWF2xdeINa',
        -- admina112
        'Nguyễn Văn A',
        '1.jpg',
        '112 đường Cao Thắng, P.4, Q.3, TP.Hồ Chí Minh',
        '0987654321',
        1
    ), (
        'tranthic@gmail.com',
        '$2a$12$At6FO7hUPLa6UwJNDTLdzuTWUgQUAAr1s4GHF8VyjPIWsNehHzdwS',
        -- leaderc459
        'Trần Thị C',
        '2.jpg',
        '459 đường Sư Vạn Hạnh, P.12, Q.10, TP.Hồ Chí Minh',
        '0123456789',
        2
    ), (
        'levand@gmail.com',
        '$2a$12$Xqrp0qa8TrpuEiAxNL.fMO5jY13L3XH5l6Yd5LhPTf3fxZbH4LezK',
        -- memberd117
        'Lê Văn D',
        '3.jpg',
        '117 đường Tân Cảng, P.25, Q.Bình Thạnh, TP.Hồ Chí Minh',
        '0918273645',
        3
    ), (
        'phamthib@gmail.com',
        '$2a$12$9lErqX4Jm6yrZKr/gCycBu3CwhfPBpmrQ/9Yr6Jq2itVr2rVMShCi',
        -- leaderb110
        'Phạm Thị B',
        '4.jpg',
        '110 đường số 10, P.10, Q.Gò Vấp, TP.Hồ Chí Minh',
        '0963852741',
        2
    ), (
        'hoangvane@gmail.com',
        '$2a$12$T5Bh/g1CCny4d4YSJQKkLuO9aulogOj0cENJ14XTQ8WxQpf8uiCW2',
        -- membere56
        'Hoàng Văn E',
        '5.jpg',
        '56 đường Lê Cảnh Tuân, P.Phú Thọ Hoà, Q.Tân Phú, TP.Hồ Chí Minh',
        '0951874632',
        3
    ), (
        'huynhthie@gmail.com',
        '$2a$12$/KwjfGF8PG4shobQ3eUELup8gBPdN28k/g5/sCOQ0fa4Ahg72Xo7G',
        -- membere6c
        'Huỳnh Thị E',
        '6.jpg',
        '6C đường số 8, P.Linh Tây, Q.Thủ Đức, TP.Hồ Chí Minh',
        '0951632874',
        3
    ), (
        'nguyenvang@gmail.com',
        '$2a$12$yS7GLqyCxSNI3aJD1oiBIeCTAkTnVchpCL4QO/.tte95TP5o5ANDK',
        -- memberg103
        'Nguyễn Văn G',
        '7.jpg',
        '103 đường Nguyễn Hữu Dật, P.Hoà Cường Bắc, Q.Hải Châu, TP.Đà Nẵng',
        '0987456321',
        3
    );

-- Seed du an mau.

INSERT INTO
    du_an (
        ten,
        mo_ta,
        ngay_bat_dau,
        ngay_ket_thuc,
        id_nguoi_tao
    )
VALUES (
        'HRM_220101',
        'Human Resource Management',
        '2022-01-01',
        '2022-12-01',
        2
    ), (
        'CRM_220101',
        'Customer Relationship Management',
        '2022-01-01',
        '2022-07-01',
        4
    ), (
        'Jira_220701',
        'Godzilla (Gojira)',
        '2022-07-01',
        '2023-01-01',
        4
    );

-- Seed trang thai task mac dinh.

INSERT INTO
    trang_thai_cong_viec (ten)
VALUES ('Chưa bắt đầu'), ('Đang thực hiện'), ('Đã hoàn thành');

-- Seed cong viec mau.

INSERT INTO
    cong_viec (
        ten,
        mo_ta,
        ngay_bat_dau,
        ngay_ket_thuc,
        id_nguoi_thuc_hien,
        id_du_an,
        id_trang_thai_cong_viec
    )
VALUES (
        'Soft_HRM_220101',
        'Soft design: tạo spec tài liệu cho HRM',
        '2022-01-01',
        '2022-03-01',
        1,
        1,
        3
    ), (
        'Sys_HRM_220101',
        'System design: study, investigate, estimate, confirm, transfer, đưa ra schedule cho HRM',
        '2022-03-01',
        '2022-04-01',
        2,
        1,
        3
    ), (
        'DB_HRM_220101',
        'Tạo database cho HRM',
        '2022-04-01',
        '2022-06-01',
        3,
        1,
        3
    ), (
        'BE_HRM_220101',
        'Tạo backend cho HRM',
        '2022-06-01',
        '2022-08-01',
        5,
        1,
        2
    ), (
        'FE_HRM_220101',
        'Tạo frontend cho HRM',
        '2022-06-01',
        '2022-08-01',
        6,
        1,
        2
    ), (
        'UT_HRM_220101',
        'Unit test cho HRM',
        '2022-08-01',
        '2022-10-01',
        7,
        1,
        1
    ), (
        'ST_HRM_220101',
        'System test cho HRM',
        '2022-10-01',
        '2022-12-01',
        2,
        1,
        1
    ), (
        'Soft_CRM_220101',
        'Soft design: tạo spec tài liệu cho CRM',
        '2022-01-01',
        '2022-02-01',
        1,
        2,
        3
    ), (
        'Sys_CRM_220101',
        'System design: study, investigate, estimate, confirm, transfer, đưa ra schedule cho CRM',
        '2022-02-01',
        '2022-03-01',
        4,
        2,
        3
    ), (
        'DB_CRM_220101',
        'Tạo database cho CRM',
        '2022-03-01',
        '2022-04-01',
        3,
        2,
        3
    ), (
        'BE_CRM_220101',
        'Tạo backend cho CRM',
        '2022-04-01',
        '2022-05-01',
        5,
        2,
        3
    ), (
        'FE_CRM_220101',
        'Tạo frontend cho CRM',
        '2022-04-01',
        '2022-05-01',
        6,
        2,
        3
    ), (
        'UT_CRM_220101',
        'Unit test cho CRM',
        '2022-05-01',
        '2022-06-01',
        7,
        2,
        3
    ), (
        'ST_CRM_220101',
        'System test cho CRM',
        '2022-06-01',
        '2022-07-01',
        4,
        2,
        3
    ), (
        'Soft_Jira_220701',
        'Soft design: tạo spec tài liệu cho Jira',
        '2022-07-01',
        '2022-08-01',
        1,
        3,
        2
    ), (
        'Sys_Jira_220701',
        'System design: study, investigate, estimate, confirm, transfer, đưa ra schedule cho Jira',
        '2022-08-01',
        '2022-09-01',
        4,
        3,
        1
    ), (
        'DB_Jira_220701',
        'Tạo database cho Jira',
        '2022-09-01',
        '2022-10-01',
        3,
        3,
        1
    ), (
        'BE_Jira_220701',
        'Tạo backend cho Jira',
        '2022-10-01',
        '2022-11-01',
        5,
        3,
        1
    ), (
        'FE_Jira_220701',
        'Tạo frontend cho Jira',
        '2022-10-01',
        '2022-11-01',
        6,
        3,
        1
    ), (
        'UT_Jira_220701',
        'Unit test cho Jira',
        '2022-11-01',
        '2022-12-01',
        7,
        3,
        1
    ), (
        'ST_Jira_220701',
        'System test cho Jira',
        '2022-12-01',
        '2023-01-01',
        4,
        3,
        1
    );
