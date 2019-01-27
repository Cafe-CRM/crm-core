package com.cafe.crm.models.client;

import com.cafe.crm.models.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "setting_client")
public class SettingClient extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Boolean enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
