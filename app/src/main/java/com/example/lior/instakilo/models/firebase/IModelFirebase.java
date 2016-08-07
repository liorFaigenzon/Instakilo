package com.example.lior.instakilo.models.firebase;

import com.example.lior.instakilo.models.Model;

public interface IModelFirebase {
    void getAll(String lastUpdateDate, Model.GetAllListener listener);
    void getById();
    void add(Object model, Model.AddListener listener);
    void update(Object model, Model.UpdateListener listener);
    void delete(Object model, Model.DeleteListener listener);
}
