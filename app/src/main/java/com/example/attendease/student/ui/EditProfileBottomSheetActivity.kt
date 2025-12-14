package com.example.attendease.student.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.attendease.common.controllers.StudentController
import com.example.attendease.databinding.ActivityEditProfileBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class EditProfileBottomSheetActivity : BottomSheetDialogFragment() {

    private var _binding: ActivityEditProfileBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var name: String? = null
    private lateinit var studentController: StudentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name")
        }
        studentController = StudentController()
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


        binding.btnSaveProfile.setOnClickListener {
            val firstName = binding.etEditName.text.toString().trim()
            val lastName = binding.lastnameEditText.text.toString().trim()

            // Validate the input
            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(requireContext(), "First Name and Last Name cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            lifecycleScope.launch {
                val result = studentController.updateUserProfile(uid,firstName, lastName)
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