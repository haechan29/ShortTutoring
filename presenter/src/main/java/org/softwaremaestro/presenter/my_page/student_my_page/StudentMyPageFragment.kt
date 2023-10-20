package org.softwaremaestro.presenter.my_page.student_my_page

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import org.softwaremaestro.domain.tutoring_get.entity.TutoringVO
import org.softwaremaestro.presenter.databinding.FragmentStudentMyPageBinding
import org.softwaremaestro.presenter.login.LoginActivity
import org.softwaremaestro.presenter.login.viewmodel.LoginViewModel
import org.softwaremaestro.presenter.my_page.viewmodel.FollowerViewModel
import org.softwaremaestro.presenter.my_page.viewmodel.ProfileViewModel
import org.softwaremaestro.presenter.student_home.StudentHomeFragment
import org.softwaremaestro.presenter.student_home.adapter.LectureAdapter
import org.softwaremaestro.presenter.student_home.viewmodel.TutoringViewModel
import org.softwaremaestro.presenter.teacher_home.adapter.ReviewAdapter
import org.softwaremaestro.presenter.util.widget.ProfileImageSelectBottomDialog
import org.softwaremaestro.presenter.video_player.VideoPlayerActivity

@AndroidEntryPoint
class StudentMyPageFragment : Fragment() {

    private lateinit var binding: FragmentStudentMyPageBinding

    private val tutoringViewModel: TutoringViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val followerViewModel: FollowerViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var lectureAdapter: LectureAdapter

    private lateinit var dialog: ProfileImageSelectBottomDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentMyPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getMyProfile()
        initLectureRecyclerView()

        setBtnEditTeacherImg()
        setFollowingMenu()
        setServiceCenterMenu()
        setLogOutContainer()

        observe()
    }

    private fun observe() {
        observeTutoring()
        observeProfile()
    }

    private fun observeTutoring() {
        tutoringViewModel.tutoring.observe(requireActivity()) {
            binding.containerClipEmpty.visibility =
                if (it.isEmpty()) View.VISIBLE else View.GONE

            lectureAdapter.apply {
                setItem(it)
                notifyDataSetChanged()
            }

            binding.tvNumOfClip.text = it.size.toString()
        }
    }

    private fun observeProfile() {

        profileViewModel.name.observe(viewLifecycleOwner) {
            binding.tvStudentName.text = it
        }

        profileViewModel.schoolLevel.observe(viewLifecycleOwner) {
            binding.tvStudentSchoolLevel.text = it
        }

        profileViewModel.schoolGrade.observe(viewLifecycleOwner) {
            binding.tvStudentSchoolGrade.text = "${it}학년"
        }

        profileViewModel.image.observe(viewLifecycleOwner) {
            Glide.with(requireContext()).load(it).circleCrop().into(binding.ivStudentImg)
        }

        profileViewModel.followers.observe(viewLifecycleOwner) {
            binding.btnFollow.text = "찜한 선생님 ${it.size}명"
        }
    }

    private fun initLectureRecyclerView() {

        lectureAdapter = LectureAdapter {
            watchRecordFile(it)
        }

        binding.rvClip.apply {
            adapter = lectureAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }

        tutoringViewModel.getTutoring()
    }

    private fun watchRecordFile(tutoringVO: TutoringVO) {
        val intent = Intent(requireActivity(), VideoPlayerActivity::class.java).apply {
            putExtra(StudentHomeFragment.PROFILE_IMAGE, tutoringVO.opponentProfileImage)
            putExtra(StudentHomeFragment.STUDENT_NAME, tutoringVO.opponentName)
            putExtra(StudentHomeFragment.SCHOOL_LEVEL, tutoringVO.schoolLevel)
            putExtra(StudentHomeFragment.SUBJECT, tutoringVO.schoolSubject)
            putExtra(StudentHomeFragment.DESCRIPTION, tutoringVO.description)
            tutoringVO.recordFileUrl?.get(0)
                ?.let { putExtra(StudentHomeFragment.RECORDING_FILE_URL, it) }
        }
        startActivity(intent)
    }

    private fun setBtnEditTeacherImg() {
//        binding.containerStudentImg.setOnClickListener {
//            dialog = ProfileImageSelectBottomDialog(
//                onImageChanged = { image ->
//                    binding.ivStudentImg.setBackgroundResource(image)
//                },
//                onSelect = { res ->
//                    val image = BitmapFactory.decodeResource(resources, res).toBase64()
//                    profileViewModel.setImage(image)
////                    profileViewModel.updateProfile()
//                    dialog.dismiss()
//                },
//            )
//            dialog.show(parentFragmentManager, "profileImageSelectBottomDialog")
//        }
    }

    private fun setFollowingMenu() {
        binding.containerFollowing.setOnClickListener {
            startActivity(Intent(requireActivity(), FollowingActivity::class.java))
        }
    }

    private fun setServiceCenterMenu() {
        binding.containerServiceCenter.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                addCategory(Intent.CATEGORY_BROWSABLE)
                data = Uri.parse(SERVICE_CENTER_URL)
            }
            startActivity(intent)
        }
    }

    companion object {
        private const val SERVICE_CENTER_URL = "https://www.form.short-tutoring.com"
    }

    private fun setLogOutContainer() {
        binding.containerLogOut.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            loginViewModel.clearJWT()
            startActivity(intent)
        }
    }
}
