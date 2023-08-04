package org.softwaremaestro.presenter.teacher_home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.softwaremaestro.domain.question_get.entity.QuestionGetResponseVO
import org.softwaremaestro.presenter.databinding.ItemQuestionBinding

private const val EMPTY_STRING = "-"

class QuestionAdapter(private val answerBtnClickListener: (String) -> Unit) :
    RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    private var items: List<QuestionGetResponseVO> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionAdapter.ViewHolder {
        val view = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionAdapter.ViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItem(items: List<QuestionGetResponseVO>) {
        this.items = items
    }

    inner class ViewHolder(private val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: QuestionGetResponseVO) {

            with(binding) {

                Picasso.with(root.context).load(item.problemImage).fit().centerCrop().into(ivPhoto)

                tvSubject.text = item.problemSchoolSubject ?: EMPTY_STRING
                tvDifficulty.text = item.problemDifficulty ?: EMPTY_STRING
                tvDesciption.text = item.problemDescription ?: EMPTY_STRING

                root.setOnClickListener {
                    item.id?.let { answerBtnClickListener(it) }
                }
            }
        }
    }
}