package org.softwaremaestro.presenter.question_upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.softwaremaestro.presenter.R
import org.softwaremaestro.presenter.databinding.FragmentQuestionFormDifficultyBinding
import org.softwaremaestro.presenter.question_upload.viewmodel.QuestionUploadViewModel

class QuestionFormDifficultyFragment : Fragment() {

    private lateinit var binding: FragmentQuestionFormDifficultyBinding


    private val viewModel: QuestionUploadViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentQuestionFormDifficultyBinding.inflate(layoutInflater)
        setToolBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rgDifficulty.setOnCheckedChangeListener { _, resId ->

            val selectedDifficulty =
                when (resId) {
                    R.id.rb_easy -> "쉬움"
                    R.id.rb_middle -> "중간"
                    R.id.rb_difficult -> "어려움"
                    else -> null
                }

            viewModel._difficulty.postValue(selectedDifficulty)
            findNavController().popBackStack(R.id.questionFormFragment, false)
        }
    }

    private fun setToolBar() {
        binding.btnToolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}