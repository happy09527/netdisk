package com.easypan.entity.enums;

/**
 * @author: ZhangX
 * @createDate: 2023/7/22
 * @description:
 */
public enum UserStatusEnum {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");


    private Integer status;
    private String desc;

    private UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static UserStatusEnum getByStatus(Integer status) {
        for (UserStatusEnum item : UserStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
