package com.cafe.crm.models.template;

import com.cafe.crm.models.BaseEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

@Entity
public class Template {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    @Lob
    @Column(length = 50000)
    private byte[] content;

    public Template() {
    }

    public Template(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
