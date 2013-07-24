package com.wks.calorieapp.daos;

import java.util.List;

public interface DataAccessObject<T>
{
    public Object create(T object) throws DataAccessObjectException;
    public List<T> read() throws DataAccessObjectException;
    public T read(Object id) throws DataAccessObjectException;
    public boolean update(T object) throws DataAccessObjectException;
    public boolean delete(T object) throws DataAccessObjectException;
    
}
