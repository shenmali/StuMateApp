package com.shenmali.stumateapp.ui.home.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.shenmali.stumateapp.R
import com.shenmali.stumateapp.data.model.Student
import com.shenmali.stumateapp.data.model.isOppositeType
import com.shenmali.stumateapp.data.source.auth.AuthRepository
import com.shenmali.stumateapp.data.source.db.DbRepository
import com.shenmali.stumateapp.data.source.localstorage.LocalStorageRepository
import com.shenmali.stumateapp.databinding.ActivityProfileBinding
import com.shenmali.stumateapp.ui.MainActivity
import com.shenmali.stumateapp.ui.home.profile.edit.EditProfileActivity
import com.shenmali.stumateapp.util.snackbar
import com.shenmali.stumateapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    @Inject
    lateinit var dbRepository: DbRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var localStorageRepository: LocalStorageRepository

    private lateinit var binding: ActivityProfileBinding
    private var isEditable = false

    private lateinit var googleMap: GoogleMap
    private var homeAddressMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        // show back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isEditable = intent.getBooleanExtra("isEditable", false)

        supportActionBar?.title = if (isEditable) "My Profile" else "Student Profile"

        val student = intent.getParcelableExtra<Student>("student")!!
        populate(student)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapViewHome) as SupportMapFragment

        lifecycleScope.launch {
            googleMap = mapFragment.awaitMap()
            addLocationMarker(student.homeAddress)
        }
    }

    private fun addLocationMarker(homeAddress: Student.HomeAddress?) {
        homeAddressMarker?.remove()
        if (homeAddress == null) {
            return
        }
        homeAddressMarker = googleMap.addMarker {
            position(homeAddress.location)
            title(homeAddress.address)
        }
        googleMap.animateCamera(
            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                homeAddress.location,
                15f
            )
        )
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val student = result.data?.getParcelableExtra<Student>("student")!!
                populate(student)
                addLocationMarker(student.homeAddress)
            }
        }

    private fun populate(student: Student) {
        if (isEditable) {
            binding.buttonEditProfile.setOnClickListener {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("student", student)
                resultLauncher.launch(intent)
            }
            binding.buttonLogout.setOnClickListener {
                onClickLogout()
            }
            binding.buttonSendRequest.isVisible = false
        } else {
            binding.buttonEditProfile.isVisible = false
            binding.buttonLogout.isVisible = false
            binding.buttonSendRequest.isVisible =
                localStorageRepository.getStudent()!!.isOppositeType(student)
            binding.buttonSendRequest.text = student.matchingStatus.toString()
            binding.buttonSendRequest.isEnabled = student.matchingStatus == Student.MatchingStatus.NoRequest
            binding.buttonSendRequest.setOnClickListener {
                onClickSendRequest(student)
            }
        }

        Glide.with(this).load(student.imageUrl).placeholder(R.drawable.image_placeholder)
            .into(binding.shapeableImageView)
        binding.textViewName.text = student.fullName
        binding.textViewState.text = student.type.toString()

        if (student.education != null) {
            binding.textViewEducation.text = student.education.toString()
            binding.textViewEducation.isVisible = true
        } else {
            binding.textViewEducation.isVisible = false
        }

        // show map fragment if student is provider, else hide it
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapViewHome) as SupportMapFragment
        mapFragment.view?.isVisible = student is Student.Provider
        binding.textViewHomeAddress.isVisible = student is Student.Provider
        binding.textViewHomeAddress.text = student.homeAddress?.address

        when (student) {
            is Student.Seeker, is Student.Provider -> {
                binding.textViewHomeTitle.isVisible = true
                binding.textViewHomeTitle.text =
                    if (student is Student.Seeker) "to Stay"
                    else "to Share"
                binding.textViewHomeDistance.text =
                    if (student is Student.Seeker) "in a house ${student.availability.distanceToUniversity} km from campus"
                    else "His house, which is ${student.availability!!.distanceToUniversity} km from the campus,"
                binding.textViewHomeDistance.isVisible = true
                binding.textViewHomeTime.text =
                    if (student is Student.Seeker) "will stay for ${student.availability.availableTime} periods "
                    else " will stay for ${student.availability!!.availableTime} periods"
                binding.textViewHomeTime.isVisible = true
            }

            else -> {
                binding.textViewHomeTitle.isVisible = false
                binding.textViewHomeDistance.isVisible = false
                binding.textViewHomeTime.isVisible = false
            }
        }

        if (isEditable || student.matchingStatus is Student.MatchingStatus.MatchedRequest) {
            binding.textViewContactEmail.text = student.email

            if (student.phone != null) {
                binding.textViewContactPhone.text = student.phone
                binding.textViewContactPhone.isVisible = true
            } else {
                binding.textViewContactPhone.isVisible = false
            }
        } else {
            binding.textViewContactTitle.isVisible = false
            binding.textViewContactEmail.isVisible = false
            binding.textViewContactPhone.isVisible = false
        }
    }

    private fun onClickSendRequest(student: Student) {
        lifecycleScope.launch {
            try {
                dbRepository.sendMatchRequestTo(student.uid)
                snackbar("Match request sent successfully.")
                binding.buttonSendRequest.text = Student.MatchingStatus.SentRequest.toString()
                binding.buttonSendRequest.isEnabled = false
            } catch (e: CancellationException) {
                // ignore
            } catch (e: Exception) {
                snackbar("Failed to send match request.", isError = true)
            }
        }
    }

    private fun onClickLogout() {
        lifecycleScope.launch {
            try {
                // remove fcm token from db
                dbRepository.removeFcmToken()
                authRepository.logout()
                localStorageRepository.clearStudent()
                toast("Logged out successfully")
                val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: CancellationException) {
                // ignore
            } catch (e: Exception) {
                snackbar("Failed to log out", isError = true)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}