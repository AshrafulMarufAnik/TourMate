package com.anik.example.tourmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.example.tourmate.Activity.AddTourActivity;
import com.anik.example.tourmate.Activity.PhoneLocationMapActivity;
import com.anik.example.tourmate.Activity.PhoneUserLocationSearchActivity;
import com.anik.example.tourmate.PlaceAPI.Prediction;
import com.anik.example.tourmate.PlaceAPI.PredictionInterface;
import com.anik.example.tourmate.PlaceAPI.Predictions;
import com.anik.example.tourmate.R;
import com.anik.example.tourmate.retrofit.ApiService;
import com.anik.example.tourmate.retrofit.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PhoneUserLocationAutoComplete extends RecyclerView.Adapter<PhoneUserLocationAutoComplete.ViewHolder> implements Filterable {

    private Context context;
    private List<Prediction> predictions;
    private PredictionInterface predictionInterface;
    private SharedPreferences sharedPreferences;

    public PhoneUserLocationAutoComplete(Context context, List<Prediction> predictions, PredictionInterface predictionInterface) {
        this.context = context;
        this.predictions = predictions;
        this.predictionInterface = predictionInterface;
    }

    @NonNull
    @Override
    public PhoneUserLocationAutoComplete.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.model_location_item,viewGroup,false);
        return new PhoneUserLocationAutoComplete.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhoneUserLocationAutoComplete.ViewHolder viewHolder, int i) {
        if (predictions != null && predictions.size() > 0) {
            final Prediction prediction = predictions.get(i);
            viewHolder.locationNameTV.setText(prediction.getDescription());
            viewHolder.locationNameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String location = prediction.getDescription();
                    Intent intent = new Intent(context, PhoneLocationMapActivity.class);
                    intent.putExtra("fromPhoneUserPlaceSearch",1);
                    intent.putExtra("phoneUserLocation",location);
                    context.startActivity(intent);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    @Override
    public Filter getFilter() {
        return new PhoneUserLocationAutoComplete.PlacesAutoCompleteFilter(this,context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationNameTV;

        public ViewHolder(View itemView) {
            super(itemView);
            locationNameTV = itemView.findViewById(R.id.locationNameTV);
        }
    }

    private class PlacesAutoCompleteFilter extends Filter {

        private PhoneUserLocationAutoComplete phoneUserLocationAutoCompleteAdapter;
        private Context context;

        public PlacesAutoCompleteFilter(PhoneUserLocationAutoComplete phoneUserLocationAutoCompleteAdapter, Context context) {
            super();
            this.phoneUserLocationAutoCompleteAdapter = phoneUserLocationAutoCompleteAdapter;
            this.context = context;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            try {
                phoneUserLocationAutoCompleteAdapter.predictions.clear();
                FilterResults filterResults = new FilterResults();
                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.values = new ArrayList<Prediction>();
                    filterResults.count = 0;
                } else {
                    ApiService googleMapAPI = RetrofitInstance.getRetrofitInstanceForPlaceAPI().create(ApiService.class);
                    Predictions predictions = googleMapAPI.getPlacesAutoComplete(charSequence.toString(), "establishment", "23.7808875,90.2792371", "500", context.getString(R.string.place_api_key)).execute().body();
                    filterResults.values = predictions.getPredictions();
                    filterResults.count = predictions.getPredictions().size();
                }
                return filterResults;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            phoneUserLocationAutoCompleteAdapter.predictions.clear();
            if (filterResults!=null){
                phoneUserLocationAutoCompleteAdapter.predictions.addAll((List<Prediction>) filterResults.values);
                phoneUserLocationAutoCompleteAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Prediction prediction = (Prediction) resultValue;
            return prediction.getDescription();
        }
    }
}