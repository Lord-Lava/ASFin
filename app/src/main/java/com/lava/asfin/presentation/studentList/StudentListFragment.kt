package com.lava.asfin.presentation.studentList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lava.asfin.R
import com.lava.asfin.data.local.Student
import com.lava.asfin.data.remote.dto.toStudentDetail
import com.lava.asfin.databinding.FragmentStudentListBinding
import com.lava.asfin.presentation.adapters.ScoreListener
import com.lava.asfin.presentation.adapters.StudentAdapter
import com.lava.asfin.presentation.adapters.StudentListener
import com.lava.asfin.util.Config.QUERY_PAGE_SIZE
import com.lava.asfin.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentListFragment : Fragment () {

    private val viewModel: StudentListViewModel by viewModels()
    private lateinit var adapter: StudentAdapter
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false
    private var pageNumber = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentStudentListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_student_list, container, false
        )

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.studentResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    pageNumber = response.value.nextPage.toInt()
                    viewModel.insertStudents(response.value.toStudentDetail())
                    hideProgressBar(binding.paginationProgressBar)
                    isLastPage = response.value.nextPage.toInt() == 50
                }
                is Resource.Loading -> {
                    showProgressBar(binding.paginationProgressBar)
                }
                is Resource.Failure -> {
                    hideProgressBar(binding.paginationProgressBar)
                    if (response.isNetworkError) {
                        Toast.makeText(context, "Check your internet connection!", Toast.LENGTH_SHORT).show()
                        return@observe
                    }
                    Toast.makeText(context, "Error: ${response.errorCode} - Couldn't fetch student details!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.rvStudentList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = StudentAdapter(
            StudentListener { serialNumber ->
                viewModel.onStudentClicked(serialNumber)
            },
            ScoreListener { score, student ->
                viewModel.setStudentScore(student, score)
            }
        )
        binding.rvStudentList.adapter = adapter
        binding.rvStudentList.addOnScrollListener(this@StudentListFragment.scrollListener)

        viewModel.getStudentsList().observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitStudentList(it)
            }
        }

        viewModel.navigateToStudentDetailFragment.observe(viewLifecycleOwner) { serialnumber ->
            serialnumber?.let {
                this.findNavController().navigate(
                    StudentListFragmentDirections.actionStudentListFragmentToStudentDetailFragment(serialnumber)
                )
                viewModel.onStudentDetailNavigated()
            }
        }

        viewModel.isValidScore.observe(viewLifecycleOwner) {
            if (it == true) Toast.makeText(context, "Score Updated Successfully!", Toast.LENGTH_SHORT).show()
            else if (it == false) Toast.makeText(context, "Please enter a score first!", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val shouldPaginate = shouldPaginate(layoutManager)

            if (shouldPaginate) {
                viewModel.getStudentsResponse(pageNumber)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun hideProgressBar(paginationProgressBar: ProgressBar) {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(paginationProgressBar: ProgressBar) {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun shouldPaginate(layoutManager: LinearLayoutManager): Boolean {
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount

        val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
        val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
        val isNotAtBeginning = firstVisibleItemPosition >= 0
        val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
        return isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                isTotalMoreThanVisible && isScrolling
    }
}