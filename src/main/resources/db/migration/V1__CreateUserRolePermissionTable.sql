CREATE TABLE users
(
    id                 serial PRIMARY KEY,
    username           VARCHAR(50) UNIQUE NOT NULL,
    encrypted_password VARCHAR(50)        NOT NULL,
    created_at         TIMESTAMP          NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP          NOT NULL DEFAULT NOW(),
    status             VARCHAR(10)        NOT NULL DEFAULT 'OK'-- OK DELETED
);
-- 定义三个用户
INSERT INTO users(id, username, encrypted_password)
VALUES (1, 'Student1', '');
INSERT INTO users(id, username, encrypted_password)
VALUES (2, 'Teacher2', '');
INSERT INTO users(id, username, encrypted_password)
VALUES (3, 'Admin3', '');

alter
sequence users_id_seq restart with 4;

CREATE TABLE role
(
    id         serial PRIMARY KEY,
    name       VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP          NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP          NOT NULL DEFAULT NOW(),
    status     VARCHAR(10)        NOT NULL DEFAULT 'OK'-- OK DELETED
);
-- 定义三种角色
INSERT INTO role(id, name)
VALUES (1, '学生');
INSERT INTO role(id, name)
VALUES (2, '老师');
INSERT INTO role(id, name)
VALUES (3, '管理员');

alter
sequence role_id_seq restart with 4;

CREATE TABLE user_role
(
    id         serial PRIMARY KEY,
    user_id    INTEGER     NOT NULL,
    role_id    INTEGER     NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    status     VARCHAR(10) NOT NULL DEFAULT 'OK'-- OK DELETED
);
-- 添加用户对应的角色信息
INSERT INTO user_role(user_id, role_id)
VALUES (1, 1);
INSERT INTO user_role(user_id, role_id)
VALUES (2, 2);
INSERT INTO user_role(user_id, role_id)
VALUES (3, 3);

alter
sequence user_role_id_seq restart with 4;

CREATE TABLE permission
(
    id         serial PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    role_id    INTEGER     NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    status     VARCHAR(10) NOT NULL DEFAULT 'OK'-- OK DELETED
);
-- 添加权限 什么角色有什么权限
INSERT INTO permission(name, role_id)
VALUES ('登录用户', 1);
INSERT INTO permission(name, role_id)
VALUES ('登录用户', 2);
INSERT INTO permission(name, role_id)
VALUES ('登录用户', 3);
INSERT INTO permission(name, role_id)
VALUES ('上传课程', 2);
INSERT INTO permission(name, role_id)
VALUES ('上传课程', 3);
INSERT INTO permission(name, role_id)
VALUES ('管理用户', 3);