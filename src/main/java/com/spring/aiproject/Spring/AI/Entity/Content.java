package com.spring.aiproject.Spring.AI.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;

@Getter
@Setter
public class Content {
    private String title;
    private String content;
    private String code;

    public Content( String title,String content, String code) {
        this.content = content;
        this.title = title;
        this.code = code;
    }

    public Content() {
    }
}
