package com.lava.asfin.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lava.asfin.data.local.Student
import com.lava.asfin.databinding.ItemStudentBinding
import com.lava.asfin.generated.callback.OnClickListener
import com.lava.asfin.presentation.studentList.StudentListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StudentAdapter(
    private val clickListener: StudentListener,
    private val scoreListener: ScoreListener
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(StudentDiffCallBack()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val studentItem = getItem(position) as DataItem.StudentItem
                holder.bind(scoreListener, clickListener, studentItem.student)
            }
        }
    }

    fun submitStudentList(list: List<Student>) {
        adapterScope.launch {
            val items = list.map {
                DataItem.StudentItem(it)
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class ViewHolder private constructor(
        private val binding: ItemStudentBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(scoreListener: ScoreListener, clickListener: StudentListener, item: Student) {
            binding.student = item
            binding.clickListener = clickListener
            binding.scoreListener = scoreListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemStudentBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class StudentDiffCallBack : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class StudentListener(
    val clickListener: (serialnumber: Int) -> Unit
) {
    fun onClick(student: Student) = clickListener(student.serialnumber)
}

class ScoreListener(
    val clickListener: (score: String, student: Student) -> Unit
) {
    fun onClick(editText: EditText, student: Student) {
        clickListener(editText.text.toString(), student)
        editText.text.clear()
    }
}

sealed class DataItem {
    data class StudentItem(val student: Student) : DataItem() {
        override val id = student.serialnumber
    }

    abstract val id: Int
}

