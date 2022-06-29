package com.lava.asfin.presentation.studentList

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lava.asfin.R
import com.lava.asfin.data.remote.dto.toStudentDetail
import com.lava.asfin.databinding.FragmentStudentListBinding
import com.lava.asfin.presentation.adapters.ScoreListener
import com.lava.asfin.presentation.adapters.StudentAdapter
import com.lava.asfin.presentation.adapters.StudentListener
import com.lava.asfin.util.Config.QUERY_LAST_PAGE
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
    private var pageNumber = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentStudentListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_student_list, container, false
        )

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        viewModel.studentResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    val studentDetails = response.value.toStudentDetail()
                    Log.d("snehil", pageNumber)
                    pageNumber = studentDetails.nextPage
                    viewModel.insertStudents(studentDetails)
                    hideProgressBar(binding.paginationProgressBar)
                    isLastPage = studentDetails.nextPage.toInt() == QUERY_LAST_PAGE
                }
                is Resource.Loading -> {
                    showProgressBar(binding.paginationProgressBar)
                }
                is Resource.Failure -> {
                    hideProgressBar(binding.paginationProgressBar)
                    if (response.isNetworkError) {
                        Toast.makeText(context, getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show()
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
            if (it == true) Toast.makeText(context, getString(R.string.success_score_update_text), Toast.LENGTH_SHORT).show()
            else if (it == false) Toast.makeText(context, getString(R.string.empty_score_warning_text), Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (menu is MenuBuilder) menu.setOptionalIconsVisible(true)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear -> {
                viewModel.deleteAllStudents()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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