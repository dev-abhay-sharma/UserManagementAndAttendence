package com.chetna.ngo.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.activities.AddCaseHistoryActivity;
import com.chetna.ngo.activities.AddEventActivity;
import com.chetna.ngo.activities.EditProject;
import com.chetna.ngo.activities.OpenFragmentsActivity;
import com.chetna.ngo.activities.ProjectListActivity;
import com.chetna.ngo.adapters.SliderAdapterExample;
import com.chetna.ngo.models.SliderItem;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminFragment extends Fragment {

    private Context context;
    private TextView tvAdminMessage;

    SliderView sliderView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SliderAdapterExample adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminFragment newInstance(String param1, String param2) {
        AdminFragment fragment = new AdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        LinearLayout Lyt = view.findViewById(R.id.adminLyt);
        tvAdminMessage = Lyt.findViewById(R.id.marqueText);
        sliderView = Lyt.findViewById(R.id.imageSlider);
        loadSlider();
        getAdminMessage();
        view.findViewById(R.id.addProjectLyt)
                .setOnClickListener(view1 -> startActivity(new Intent(context, EditProject.class)
                .putExtra("status","create")));
        view.findViewById(R.id.projectListLyt)
                .setOnClickListener(view1 -> startActivity(new Intent(context, ProjectListActivity.class).putExtra("from", "p")));
        view.findViewById(R.id.lytAddCaseHistory)
                .setOnClickListener(view1 -> startActivity(new Intent(context, AddCaseHistoryActivity.class)));
        view.findViewById(R.id.addEvent)
                .setOnClickListener(view1 -> startActivity(new Intent(context, AddEventActivity.class)));
        view.findViewById(R.id.changeMessageLayout)
                .setOnClickListener(view12 -> {
                    showAddDialog();
                });
        view.findViewById(R.id.chnageSliderImage)
                .setOnClickListener(view12 -> {
                    Intent intent = new Intent(context, OpenFragmentsActivity.class);
                    intent.putExtra("type", "Change Slider Images");
                    intent.putExtra("project_id", "0");
                    context.startActivity(intent);
                });

        view.findViewById(R.id.lytCreateSubAdmin).setOnClickListener(v -> {
            Intent intent = new Intent(context, OpenFragmentsActivity.class);
            intent.putExtra("type", "create_sub_admin");
            context.startActivity(intent);
        });
        view.findViewById(R.id.asign_project_sub_admin).setOnClickListener(v -> {
            Intent intent = new Intent(context, ProjectListActivity.class);
            intent.putExtra("from", "assign_sub_admin");
            context.startActivity(intent);
        });

        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_SUB_ADMIN)) {
            view.findViewById(R.id.admin_main_lyt).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.admin_main_lyt).setVisibility(View.VISIBLE);
        }

        return view;
    }


    private void showConfirmDialog(String name, AlertDialog Dialog) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Confirm!");
        dialogBuilder.setMessage("Change message");
        dialogBuilder.setIcon(R.drawable.ic_check);
        dialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            Dialog.dismiss();
            changeMessage(name);
        });
        dialogBuilder.setNegativeButton("No!", (dialog, which) -> {
            dialog.dismiss();
        });
        dialogBuilder.show();
    }

    private void changeMessage(String name) {
        Log.e("TAG", "changeMessage: " + Constants.getString(context, Constants.USER_TYPE));

        Constants.showProgressDialog("Changing Message!", "Please Wait...", context);


        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.ADD_MESSAGE_ADMIN, response -> {
            Log.e("TAG", "changeMessage: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));

            } catch (JSONException e) {
                e.printStackTrace();
                Constants.hideProgressDialog();
            }

        }, error -> {
            Constants.hideProgressDialog();
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(context, "Please Check your Internet connection");
                return;
            }
            Constants.showToast(context, error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("message", name);
                params.put("user_id", Constants.getString(context, Constants.USER_ID));
                Log.e("PARAMS", "getParams: " + params);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        request.setRetryPolicy(new
                DefaultRetryPolicy(
                Constants.CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);


    }


    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_dialog_lyt, null);
        dialogBuilder.setView(dialogView);

        EditText editText = (EditText) dialogView.findViewById(R.id.edWorkingAreaName);
        AppCompatButton bAdd = (AppCompatButton) dialogView.findViewById(R.id.bAddArea);
        TextView tv = dialogView.findViewById(R.id.tvTitle);
        tv.setText("Change Message!");
        AlertDialog alertDialog = dialogBuilder.create();
        bAdd.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                editText.setError("Enter Area Name");
                return;
            }
            showConfirmDialog(editText.getText().toString(), alertDialog);
        });
        alertDialog.show();

    }

    private void loadSlider() {
        adapter = new SliderAdapterExample(context);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        getSliderData();
    }

    private void getSliderData() {
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_SLIDER_IMAGE, response -> {
            Log.e("TAG", "changeMessage: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray requestsData;
                requestsData = jsonObject.getJSONArray("data");
                if (requestsData.length() > 0) {
                    for (int i = 0; i < requestsData.length(); i++) {
                        JSONObject object = requestsData.getJSONObject(i);
                        SliderItem model = new SliderItem(object.getString("text"), object.getString("image"), object.getString("id"), object.getString("text"), object.getString("project_id"));
                        adapter.addItem(model);
                    }
                }
                Constants.showToast(context, jsonObject.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
                Constants.hideProgressDialog();
            }

        }, error -> {
            Constants.hideProgressDialog();
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(context, "Please Check your Internet connection");
                return;
            }
            Constants.showToast(context, error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", "0");
                Log.e("PARAMS", "getParams: " + params);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        request.setRetryPolicy(new
                DefaultRetryPolicy(
                Constants.CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

    }

    private void getAdminMessage() {
        Constants.showProgressDialog("Loading Data!", "Please Wait.....", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_ADMIN_MESSAGE, response -> {
            Log.e("TAG", "getAdminMessage: " + response);
            Constants.hideProgressDialog();
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    JSONObject userData = jsonObject.getJSONObject("data");
                    tvAdminMessage.setText(userData.getString("message").toString());

                    Log.e("TAG", "getAdminMessage: " + userData);
                }
            } catch (JSONException e) {
                Constants.hideProgressDialog();
                Log.e("TAG", "getAdminMessage: " + e.getLocalizedMessage());
            }
        }, error -> {
            Constants.hideProgressDialog();
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(context, "Please Check your Internet connection");
                return;
            }
            Constants.showToast(context, error.getLocalizedMessage());
        });
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        request.setRetryPolicy(new
                DefaultRetryPolicy(
                Constants.CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);


    }

}