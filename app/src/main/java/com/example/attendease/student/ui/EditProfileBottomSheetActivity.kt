package com.example.attendease.student.ui

import com.example.attendease.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.attendease.common.controllers.StudentController
import com.example.attendease.common.network.ApiClient
import com.example.attendease.common.network.ApiService
import com.example.attendease.common.network.model.Course
import com.example.attendease.databinding.ActivityEditProfileBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditProfileBottomSheetActivity : BottomSheetDialogFragment() {

    private var _binding: ActivityEditProfileBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var name: String? = null
    private var select: Course? = null
    private lateinit var studentController: StudentController
    private var coursesCache: List<Course> = emptyList()
    private lateinit var apiService: ApiService



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name")
        }
        studentController = StudentController()
        apiService = ApiClient.instance


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityEditProfileBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCourses(apiService)

        lifecycleScope.launch {
            val firebaseUid = FirebaseAuth.getInstance().currentUser!!.uid

            val result = studentController.getUserProfile(firebaseUid)

            if (result.isSuccess) {
                val student = result.getOrNull()
                binding.etEditName.setText(student?.firstname)
                binding.lastnameEditText.setText(student?.lastname)

            } else {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }

        binding.courseDropdown



        binding.btnSaveProfile.setOnClickListener {
            val firstName = binding.etEditName.text.toString().trim()
            val lastName = binding.lastnameEditText.text.toString().trim()

            // Validate the input
            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(requireContext(), "First Name and Last Name cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""


            val course_id = select?.course_id
            if (course_id == null) {
                binding.userCourse.error = "Please select a course"
                return@setOnClickListener
            } else {
                binding.userCourse.error = null
            }
            lifecycleScope.launch {
                val result = studentController.updateUserProfile(uid,firstName, lastName,course_id)
                if (result.isSuccess) {

                    parentFragmentManager.setFragmentResult(
                        "profileUpdated",   // key
                        Bundle.EMPTY        // no data needed, or you can pass updated names
                    )

                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Profile Updated")
                        .setMessage("Profile updated successfully!")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            dismiss()
                        }
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun loadCourses(apiService: ApiService) {
        binding.userCourse.isEnabled = false
        binding.userCourse.error = null
        binding.userCourse.helperText = "Loading courses..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getCourses()
                }

                if (response.isSuccessful) {
                    val courses = response.body()?.courses.orEmpty()
                    coursesCache = courses

                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        courses.map { it.name }
                    )

                    binding.courseDropdown.setAdapter(adapter)

                    binding.courseDropdown.setOnItemClickListener { _, _, position, _ ->
                         select = coursesCache[position]

                    }

                    binding.userCourse.helperText = null
                    binding.userCourse.isEnabled = true
                } else {
                    binding.userCourse.isEnabled = true
                    binding.userCourse.helperText = null
                    binding.userCourse.error = "Failed to load courses (${response.code()})"
                }

            } catch (e: Exception) {
                binding.userCourse.isEnabled = true
                binding.userCourse.helperText = null
                binding.userCourse.error = "Network error: ${e.message ?: "Unknown"}"
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(name: String?): EditProfileBottomSheetActivity {
            val fragment = EditProfileBottomSheetActivity()
            val args = Bundle().apply {
                putString("name", name)
            }
            fragment.arguments = args
            return fragment
        }
    }
}