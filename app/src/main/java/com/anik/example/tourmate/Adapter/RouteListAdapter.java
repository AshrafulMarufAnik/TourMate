package com.anik.example.tourmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.example.tourmate.Activity.TourHistoryActivity;
import com.anik.example.tourmate.ModelClass.Route;
import com.anik.example.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.ViewHolder> {
    private ArrayList<Route> routeList;
    private Context context;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public RouteListAdapter(ArrayList<Route> routeList, Context context) {
        this.routeList = routeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.single_route_point_item_view,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Route currentRoute = routeList.get(position);

        String routeID = currentRoute.getRouteID();
        holder.routeName.setText(currentRoute.getRoutePointName());

        holder.routeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = context.getSharedPreferences("TourInfo",MODE_PRIVATE);
                final String tourID = sharedPreferences.getString("SPTourID",null);
                String uid = firebaseAuth.getCurrentUser().getUid();
                final DatabaseReference routeRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Route points Lists").child(currentRoute.getRouteID());

                PopupMenu popupMenu = new PopupMenu(context,holder.routeMenu);
                popupMenu.inflate(R.menu.route_option_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.routeVisited){
                            if(tourID != null){
                                routeRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(context, "Route point deleted", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                routeList.remove(position);
                                notifyDataSetChanged();
                            }
                            else {
                                context.startActivity(new Intent(context, TourHistoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            }

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView routeName;
        private ImageView routeMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            routeName = itemView.findViewById(R.id.routeNameTV);
            routeMenu = itemView.findViewById(R.id.routeMenuIV);
        }
    }
}
