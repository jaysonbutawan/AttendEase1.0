package com.example.attendease.student.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.attendease.R
import com.example.attendease.common.controllers.StudentController
import com.example.attendease.common.firebase.AuthRepository
import com.example.attendease.common.ui.auth.StudentLoginActivity
import com.example.attendease.databinding.FragmentProfileScreenBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragmentActivity : Fragment() {

    private lateinit var auth: AuthRepository
    private lateinit var binding: FragmentProfileScreenBinding
    private lateinit var studentController: StudentController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileScreenBinding.inflate(inflater, container, false)
        auth = AuthRepository(FirebaseAuth.getInstance())
        studentController = StudentController()

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            loadUserProfile(it)
        }
        parentFragmentManager.setFragmentResultListener(
            "profileUpdated",
            viewLifecycleOwner
        ) { _, _ ->
            FirebaseAuth.getInstance().currentUser?.uid?.let {
                loadUserProfile(it)
            }
        }
        binding.btnEditProfile.setOnClickListener { showEditProfileSheet() }
        setupLogoutButton()
        return binding.root
    }


    private fun loadUserProfile(firebaseUid: String) {
        lifecycleScope.launch {
            val result = studentController.getUserProfile(firebaseUid)

            if (result.isSuccess) {
                val student = result.getOrNull()

                val fullName = "${student?.firstname} ${student?.lastname}"

                binding.tvStudentName.text = fullName
                binding.tvStudentProgram.text = student?.course_name ?: "No course"

                loadProfileImage()
            } else {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProfileImage() {
        val img = FirebaseAuth.getInstance().currentUser?.photoUrl
        if (img != null) {
            Glide.with(requireContext())
                .load(img)
                .placeholder(R.drawable.ic_profile)
                .into(binding.ivStudentAvatar)
        } else {
            binding.ivStudentAvatar.setImageResource(R.drawable.ic_profile)
        }
    }

    private fun showEditProfileSheet() {
        val name = binding.tvStudentName.text.toString()
        val sheet = EditProfileBottomSheetActivity.newInstance(name)
        sheet.show(parentFragmentManager, "EditProfileBottomSheetActivity")
    }

    private fun setupLogoutButton() {
        binding.btnStudentLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { dialog, _ ->
                    auth.signOut()

                    startActivity(
                        Intent(requireContext(), StudentLoginActivity::class.java)
                            .apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                    )

                    requireActivity().finish()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }
}
