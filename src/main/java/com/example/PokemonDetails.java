package com.example;

public class PokemonDetails {
    public Types[] types;
}
// File ini buat ngasih tau Gson format objek nya

class Types {
    public Type type;
}

class Type {
    public String name;
}