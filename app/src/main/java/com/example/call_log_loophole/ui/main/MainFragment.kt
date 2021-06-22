package com.example.call_log_loophole.ui.main

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.call_log_loophole.R
import com.example.call_log_loophole.models.User

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: UserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initRecyclerView()

        viewModel.users.observe(this, Observer { users ->
            users.isEmpty().let {
                requireView().findViewById<RecyclerView>(R.id.recycler_view).isVisible = !it
                requireView().findViewById<TextView>(R.id.message).isVisible = it
            }
            adapter.submitList(users)
            requireView().findViewById<TextView>(R.id.message).text = getString(R.string.no_call_logs)
        })
    }

    private fun initRecyclerView() {
        adapter = UserAdapter()
        requireView().findViewById<RecyclerView>(R.id.recycler_view).layoutManager = LinearLayoutManager(this.requireContext())
        requireView().findViewById<RecyclerView>(R.id.recycler_view).addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val dimen = resources.getDimensionPixelSize(R.dimen.card_margin)
                with(outRect) {
                    if (parent.getChildAdapterPosition(view) == 0) top = dimen
                    left =  dimen
                    right = dimen
                    bottom = dimen
                }
            }
        })
        requireView().findViewById<RecyclerView>(R.id.recycler_view).adapter = adapter
        requireView().findViewById<TextView>(R.id.message).text = getString(R.string.loading)
    }

    inner class UserAdapter: ListAdapter<User, UserAdapter.UserViewHolder>(USER_DIFF) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val layoutInflater = LayoutInflater.from(context)
            return UserViewHolder(layoutInflater.inflate(R.layout.user_layout, parent, false))
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = getItem(position)
            holder.bind(user)
        }

        inner class UserViewHolder(view: View): RecyclerView.ViewHolder(view) {
            fun bind(user: User) {
                itemView.findViewById<TextView>(R.id.phoneNumber).text = user.phoneNumber
                itemView.findViewById<View>(R.id.delete_user_button).setOnClickListener { viewModel.delete(user) }
            }
        }
    }

    companion object {
        fun newInstance() = MainFragment()

        private val USER_DIFF = object : DiffUtil.ItemCallback<User>() {
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return true
            }

            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }
}
