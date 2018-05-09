package com.kluev.wordmemorizer.web.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Dictionary implements Serializable {

    @Id
    private Integer id;
    private String name;
    private boolean isSaved; //флаг, отмечающий, сохранён ли текущий словарь в БД

    public static Dictionary byName(String name) {
        Dictionary d = new Dictionary();
        d.setName(name);
        return d;
    }

    public Integer getId() {
        return id;
    }

    public Dictionary setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Dictionary setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "name='" + name + '\'' +
                '}';
    }
}
