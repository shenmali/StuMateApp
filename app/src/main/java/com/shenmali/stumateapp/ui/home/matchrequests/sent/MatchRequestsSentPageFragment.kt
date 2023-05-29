package com.shenmali.stumateapp.ui.home.matchrequests.sent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shenmali.stumateapp.data.model.MatchRequest
import com.shenmali.stumateapp.data.source.db.DbRepository
import com.shenmali.stumateapp.databinding.FragmentMatchRequestsSentPageBinding
import com.shenmali.stumateapp.ui.home.profile.ProfileActivity
import com.shenmali.stumateapp.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MatchRequestsSentPageFragment : Fragment() {

    @Inject
    lateinit var dbRepository: DbRepository

    private lateinit var binding: FragmentMatchRequestsSentPageBinding

    private lateinit var adapter: SentMatchRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMatchRequestsSentPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchMatchRequests()
    }

    private fun fetchMatchRequests() {
        lifecycleScope.launch {
            binding.recyclerView.isVisible = false
            binding.textViewEmpty.isVisible = false
            try {
                val requests = dbRepository.getSentMatchRequests()
                listRequests(requests)
            } catch (e: CancellationException) {
                // ignore
            } catch (e: Exception) {
                activity?.snackbar(e.message.toString(), isError = true)
            }
            binding.progressBar.hide()
            binding.recyclerView.isVisible = true
        }

    }

    private fun listRequests(requests: List<MatchRequest.Sent>) {
        binding.textViewEmpty.isVisible = requests.isEmpty()
        adapter = SentMatchRequestAdapter(
            requests.toMutableList(),
            onStudentClick = { student ->
                // navigate to student profile
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                intent.putExtra("student", student)
                startActivity(intent)
            },
            onRevoke = { request ->
                // show confirmation dialog
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Withdraw match request")
                    .setMessage("Are you sure you want to withdraw the match request you sent to student named ${request.targetStudent.fullName} ?")
                    .setPositiveButton("Yes") { _, _ ->
                        revokeMatchRequest(request)
                    }
                    .setNegativeButton("Cancel", null)
                    .create()

                dialog.show()
            },
        )
        binding.recyclerView.adapter = adapter
    }

    private fun revokeMatchRequest(request: MatchRequest.Sent) {
        lifecycleScope.launch {
            try {
                dbRepository.revokeMatchRequest(request.uid)
                onMatchRequestRevoked(request)
            } catch (e: CancellationException) {
                // ignore
            } catch (e: Exception) {
                activity?.snackbar(e.message.toString(), isError = true)
            }
        }
    }

    private fun onMatchRequestRevoked(request: MatchRequest.Sent) {
        // remove request from list
        adapter.removeItem(request)
        binding.textViewEmpty.isVisible = adapter.itemCount == 0
        activity?.snackbar("Pairing request withdrawn")
    }

}