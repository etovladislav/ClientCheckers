package ru.kpfu.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by etovladislav on 05.06.16.
 */
@XmlRootElement(name = "user_token")
@XmlType(propOrder = {"token"})
public class TokenXml {

    private String token;


    public TokenXml() {
    }

    public String getToken() {
        return token;
    }

    @XmlElement
    public void setToken(String token) {
        this.token = token;
    }
}
