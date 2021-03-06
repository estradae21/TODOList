//https://github.com/tensor-programming/Kotlin_Todo_Part2/blob/master/app/src/main/java/tensor_programming/kttodo/MainActivity.kt
//https://www.youtube.com/watch?v=OfwG2bXTusw&list=PLJbE2Yu2zumB1WoKjp22NCv5mQaz0yFQE&index=5

package com.example.ernesto.todolist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.ernesto.todolist.model.TodoModel
import com.example.ernesto.todolist.model.Visibility
import com.example.ernesto.todolist.store.Renderer
import com.example.ernesto.todolist.store.TodoStore
import android.arch.core.util.Function
import android.arch.lifecycle.Observer
import com.example.ernesto.todolist.model.AddTodo


class MainActivity : AppCompatActivity(), Renderer<TodoModel> {
    private lateinit var store: TodoStore


    override fun render(model: LiveData<TodoModel>) {
        model.observe(this, Observer { newState ->
            listView.adapter = TodoAdapter(this, newState?.todos ?: listOf())
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        store = ViewModelProviders.of(this).get(TodoStore::class.java)
        store.subscribe(this, mapStateToProps)

        addButton.setOnClickListener {
            store.dispatch(AddTodo(editText.text.toString()))
            editText.text = null
        }
        fab.setOnClickListener{ openDialog() }

        listView.adapter = TodoAdapter(this, listOf())
        listView.setOnItemClickListener({ _, _, _, id ->
            store.dispatch(ToggleTodo(id))
        })

        listView.setOnItemLongClickListener { _, _, _, id ->
            store.dispatch(RemoveTodo(id))
            true
        }
    }

    private fun openDialog() {
        val options = resources.getStringArray(R.array.filter_options).asList()
        selector(getString(R.string.filter_title), options, { _, i ->
            val visible = when(i) {
                1 -> Visibility.Active()
                2 -> Visibility.Completed()
                else -> Visibility.All()
            }
            store.dispatch(SetVisibility(visible))
        })
    }

    private val mapStateToProps = Function<TodoModel, TodoModel> {
        val keep: (Todo) -> Boolean = when(it.visibility) {

            is Visibility.All -> {_ -> true}
            is Visibility.Active -> {t: Todo -> !t.status}
            is Visibility.Completed -> {t: Todo -> t.status}
        }


        return@Function it.copy(todos = it.todos.filter { keep(it) })
    }
}