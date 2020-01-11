package co.com.pagatodo.core.views.components.dialogs.stub

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import co.com.pagatodo.core.R
import android.view.Gravity
import android.graphics.Point
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.data.model.StubModel
import co.com.pagatodo.core.views.adapters.StubAdapter
import co.com.pagatodo.core.views.stub.StubViewModel
import kotlinx.android.synthetic.main.fragment_stub_dialog.view.*

class StubDialogFragment : DialogFragment() {

    private val widthPercentage = 0.97
    private val heightPercentage = 0.95
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mainView: View

    lateinit var adapter: StubAdapter

    private lateinit var viewModel: StubViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.fragment_stub_dialog, container, false)

        setupModel()
        setupView(mainView)
        dialog.setTitle("Colillas Disponibles")
        return mainView
    }

    override fun onResume() {
        super.onResume()
        setLayoutDialog()
    }

    private fun setLayoutDialog() {
        val window = dialog.window
        val size = Point()

        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)

        val width = (size.x * widthPercentage).toInt()
        val height = (size.y * heightPercentage).toInt()

        window.setLayout(width, height)
        window.setGravity(Gravity.CENTER)
    }


    private fun setupModel() {
        //val authRepository = AuthRepository()
        //val authInteractor = AuthInteractor(authRepository)
        viewModel = ViewModelProviders.of( this).get(StubViewModel::class.java)
        //viewModel.setAuthInteractor(authInteractor)
        viewModel.stubLiveData.observe(this, Observer <List<StubModel>> {
            updateStubsList(it)
        })

        viewModel.loadAllStubs()
    }


    private fun setupView(mainView: View) {
        mainView.btnOk.setOnClickListener(::dismissDialog)

        mainView.rvStubs.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun updateStubsList(data: List<StubModel>) {
        mainView.rvStubs.adapter = StubAdapter(data)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StubDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(bundle: Bundle?) =
            StubDialogFragment().apply {
                arguments = bundle
            }
    }

    private fun dismissDialog(view: View) {
        dialog.dismiss()
    }
}
