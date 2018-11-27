package com.example.ernesto.todolist.store

import android.arch.core.util.Function
import com.example.ernesto.todolist.model.Action

interface Store<T> {
    fun dispatch(action: Action)

    fun subscribe(renderer: Renderer<T>, func: Function<T, T> = Function { it })
}