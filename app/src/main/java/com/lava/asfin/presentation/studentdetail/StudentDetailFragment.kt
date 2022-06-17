package com.lava.asfin.presentation.studentdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lava.asfin.R
import com.lava.asfin.databinding.FragmentStudentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentDetailFragment : Fragment() {

    private val viewModel: StudentDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentStudentDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_student_detail, container, false
        )

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        viewModel.navigateToStudentList.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController().navigate(
                    StudentDetailFragmentDirections.actionStudentDetailFragmentToStudentListFragment()
                )
                viewModel.doneNavigating()
            }
        }

        return binding.root
    }
}