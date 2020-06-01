package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaimezhong on 11/18/16.
 */
public class Intent {
    private Integer id;
    private String name;
    private List<IntentComponent> components;

    public Intent() {
        components = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IntentComponent> getComponents() {
        return components;
    }

    public void setComponents(List<IntentComponent> components) {
        this.components = components;
    }
}
