package com.example.attendease.teacher.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.attendease.R
import com.example.attendease.common.controllers.TeacherController
import com.example.attendease.common.firebase.AuthRepository
import com.example.attendease.databinding.TeacherDialogEditProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EditProfileBottomSheet : BottomSheetDialogFragment() {

    private var _binding: TeacherDialogEditProfileBinding? = null
    private val binding get() = _binding!!

    private var name: String? = null
    private var image: String? = null
    private var email: String? = null
    private lateinit var teacherController: TeacherController

    override fun getTheme(): Int = R.style.AppTheme_BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        teacherController = TeacherController()
        arguments?.let {
            name = it.getString("name")
            email = it.getString("email")
            image = it.getString("image")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TeacherDialogEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val firebaseUid = FirebaseAuth.getInstance().currentUser!!.uid

            val result = teacherController.getUserProfile(firebaseUid)

            if (result.isSuccess) {
                val student = result.getOrNull()
                binding.etFirstname.setText(student?.firstname)
                binding.etLastname.setText(student?.lastname)
                binding.etContactNumber.setText(student?.contact_number)

            } else {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }

        if (!image.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(image)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(binding.profileImageEdit)
        } else {
            binding.profileImageEdit.setImageResource(R.drawable.default_avatar)
        }

        binding.btnSaveProfile.setOnClickListener {
            val firstName = binding.etFirstname.text.toString().trim()
            val lastName = binding.etLastname.text.toString().trim()
            val contact_number = binding.etContactNumber.text.toString().trim()
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (firstName.isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Empty Field")
                    .setMessage("Please provide input before saving.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(true)
                    .show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = teacherController.updateUserProfile(uid,firstName, lastName,contact_number)
                if (result.isSuccess) {
                    Log.d("EditProfileBottomSheet", "Profile updated successfully + contac"+ contact_number)

                    parentFragmentManager.setFragmentResult(
                        "profileUpdated",
                        Bundle.EMPTY
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

        binding.ivEditImage.setOnClickListener {
            Toast.makeText(requireContext(), "Change image clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(name: String?, email: String?, image: String?): EditProfileBottomSheet {
            val fragment = EditProfileBottomSheet()
            val args = Bundle().apply {
                putString("name", name)
                putString("email", email)
                putString("image", image)
            }
            fragment.arguments = args
            return fragment
        }
    }
}