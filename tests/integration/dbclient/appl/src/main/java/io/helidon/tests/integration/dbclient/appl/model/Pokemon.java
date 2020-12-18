/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.tests.integration.dbclient.appl.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.helidon.tests.integration.dbclient.appl.model.Type.TYPES;

/**
 * Pokemon POJO.
 */
public class Pokemon {

    /** Map of pokemons. */
    public static final Map<Integer, Pokemon> POKEMONS = new HashMap<>();

    // Initialize pokemons Map
    static {
        // Pokemons for query tests
        POKEMONS.put(1, new Pokemon(1, "Pikachu", TYPES.get(13)));
        POKEMONS.put(2, new Pokemon(2, "Raichu", TYPES.get(13)));
        POKEMONS.put(3, new Pokemon(3, "Machop", TYPES.get(2)));
        POKEMONS.put(4, new Pokemon(4, "Snorlax", TYPES.get(1)));
        POKEMONS.put(5, new Pokemon(5, "Charizard", TYPES.get(10), TYPES.get(3)));
        POKEMONS.put(6, new Pokemon(6, "Meowth", TYPES.get(1)));
        POKEMONS.put(7, new Pokemon(7, "Gyarados", TYPES.get(3), TYPES.get(11)));
    }
    
    private final int id;
    private final String name;
    private final List<Type> types;

    public Pokemon(int id, String name, Type... types) {
        this.id = id;
        this.name = name;
        this.types = new ArrayList<>(types != null ? types.length : 0);
        if (types != null) {
            for (Type type : types) {
                this.types.add(type);
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Type> getTypes() {
        return types;
    }

    public Type[] getTypesArray() {
        return types.toArray(new Type[types.size()]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pokemon: {id=");
        sb.append(id);
        sb.append(", name=");
        sb.append(name);
        sb.append(", types=[");
        boolean first = true;
        for (Type type : types) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(type.toString());
        }
        sb.append("]}");
        return sb.toString();
    }

}
