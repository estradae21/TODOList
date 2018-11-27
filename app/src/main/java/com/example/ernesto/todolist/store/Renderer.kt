package com.example.ernesto.todolist.store

import android.arch.lifecycle.LiveData

interface Renderer<T> {
    fun render(model: LiveData<T>)
}