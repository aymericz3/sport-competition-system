package com.sports.app;

import java.util.*;

public class InMemoryRepository<T> {
    private final Map<String, T> store = new LinkedHashMap<>();

    public void save(String id, T entity)          { store.put(id, entity); }
    public Optional<T> find(String id)             { return Optional.ofNullable(store.get(id)); }
    public Collection<T> findAll()                 { return Collections.unmodifiableCollection(store.values()); }
    public void delete(String id)                  { store.remove(id); }
    public boolean exists(String id)               { return store.containsKey(id); }
    public int size()                              { return store.size(); }
}
