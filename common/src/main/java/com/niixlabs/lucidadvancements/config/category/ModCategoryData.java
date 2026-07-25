package com.niixlabs.lucidadvancements.config.category;

import java.util.ArrayList;
import java.util.List;

public final class ModCategoryData {
    public String mod_id;
    public List<CategoryDefinition> categories = new ArrayList<>();

    public ModCategoryData(String modId) {
        this.mod_id = modId;
    }
}