package com.example.patryk.bukrisk.user;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.patryk.bukrisk.R;
import com.example.patryk.bukrisk.Request.GetMatchesRequest;
import com.example.patryk.bukrisk.Request.GetPaymentsRequest;
import com.example.patryk.bukrisk.Request.GetTeamRequest;
import com.example.patryk.bukrisk.adapter.AcceptPaymentsCustomAdapter;
import com.example.patryk.bukrisk.adapter.Matches;
import com.example.patryk.bukrisk.adapter.Payments;
import com.example.patryk.bukrisk.adapter.ShowMatchesCustomAdapter;
import com.example.patryk.bukrisk.adapter.TeamSpinnerAdapter;
import com.example.patryk.bukrisk.adapter.Teams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by patryk on 2017-05-03.
 */

public class showMatches extends Fragment {

    ProgressDialog progressDialog;

    HashMap<Integer, String> teams;

    Matches match;

    ArrayList<Matches> matches;

    ShowMatchesCustomAdapter matchesCustomAdapter;

    Boolean getDataSucces = false;

    TextView showMatches;
    ListView matchesListView;

    View myView;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.show_matches_layout, container, false);

        showMatches = (TextView) myView.findViewById(R.id.showAllMatchesTV);
        matchesListView = (ListView) myView.findViewById(R.id.macthesListView);

        matches = new ArrayList<>();
        teams = new HashMap<>();
        matchesCustomAdapter = new ShowMatchesCustomAdapter(myView.getContext(),matches);

        matchesListView.setAdapter(matchesCustomAdapter);

        match = new Matches("Mecz / Data", "Home","Draw","Away");
        matches.add(match);
        match = new Matches("", "","","");
        matches.add(match);
        matchesCustomAdapter.notifyDataSetChanged();

        getTeam();

        return myView;
    }

    private void getTeam()
    {
        progressDialog = new ProgressDialog(myView.getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching data from the Server.");
        progressDialog.show();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);

                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                        JSONArray jsonArray = jsonResponse.getJSONArray("teams");

                        ArrayList<JSONObject> jsonValues = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            jsonValues.add(jsonArray.getJSONObject(i));

                            int id_team = jsonValues.get(i).getInt("id_team");
                            String name = jsonValues.get(i).getString("name");

                            teams.put(id_team,name);

                        }

                        progressDialog.dismiss();

                        Toast.makeText(myView.getContext(),R.string.success, Toast.LENGTH_SHORT).show();

                        getDataSucces =  true;

                        getMatches();


                    } else {

                        progressDialog.dismiss();
                        getDataSucces =  false;

                    }

                } catch (JSONException e) {

                    Toast.makeText(myView.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        };

        GetTeamRequest gTR = new GetTeamRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(myView.getContext());
        queue.add(gTR);
    }

    private void getMatches()
    {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);

                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                        JSONArray jsonArray = jsonResponse.getJSONArray("matches");

                        ArrayList<JSONObject> jsonValues = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            jsonValues.add(jsonArray.getJSONObject(i));

                            int id_home = jsonValues.get(i).getInt("id_home");
                            int id_away = jsonValues.get(i).getInt("id_away");
                            String data = jsonValues.get(i).getString("date");
                            double teamA = jsonValues.get(i).getDouble("teamA");
                            double draw = jsonValues.get(i).getDouble("draw");
                            double teamB = jsonValues.get(i).getDouble("teamB");

                            String teamAName = teams.get(id_home);
                            String teamBName = teams.get(id_away);

                            String matchName = teamAName + " - " + teamBName;

                            match = new Matches(matchName+"\n "+data,""+teamA,""+draw,""+teamB );

                            matches.add(match);
                            matchesCustomAdapter.notifyDataSetChanged();

                            //showPay.setText(showPay.getText() + " Id_pay "+id_pay + " Id_user " + id_user + "Value " + value+" \n");
                        }

                        progressDialog.dismiss();
                        Toast.makeText(myView.getContext(),R.string.success, Toast.LENGTH_SHORT).show();


                    } else {

                        progressDialog.dismiss();

                    }

                } catch (JSONException e) {

                    Toast.makeText(myView.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        };

        GetMatchesRequest matchesRequest = new GetMatchesRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(myView.getContext());
        queue.add(matchesRequest);

    }

}