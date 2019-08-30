package com.xxl.job.console.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xxl.job.console.core.util.SnowflakeId;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/**
 * TODO
 *
 * @author esun
 * @version v1.0
 * @date: 2019-06-19
 */
@Data
public class User implements UserDetails {

    public User() {
        this.id= SnowflakeId.getId();
        this.enabled = true;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户
     */
    private String userName;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 状态(是否启用)
     */
    private boolean enabled;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
