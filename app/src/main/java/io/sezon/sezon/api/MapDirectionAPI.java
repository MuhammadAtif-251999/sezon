package io.sezon.sezon.api;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import io.sezon.sezon.gmap.GMapDirection;
import io.sezon.sezon.gmap.directions.Directions;
import io.sezon.sezon.gmap.directions.Leg;
import io.sezon.sezon.gmap.directions.Route;
import io.sezon.sezon.gmap.directions.Step;
import io.sezon.sezon.model.MboxLocation;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MapDirectionAPI {

    public static Call getDirection(LatLng pickUp, LatLng destination) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getUrl(pickUp, destination, GMapDirection.MODE_DRIVING, false))
                .build();

        return client.newCall(request);
    }

    public static Call getDirectionVia(LatLng pickUp, LatLng... destination) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getUrlVia(GMapDirection.MODE_DRIVING, false, pickUp, destination))
                .build();

        return client.newCall(request);
    }

    public static Call getViaDirection(MboxLocation pickUp, ArrayList<MboxLocation> destination) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getViaUrl(pickUp, destination, GMapDirection.MODE_DRIVING, false))
                .build();

        return client.newCall(request);
    }





    public static long getDistance(Context context, String json) {
        long dist = 0;
        if (json != null) {
            Directions directions = new Directions(context);
            List<Route> routes;

            try {
                routes = directions.parse(json);
            } catch (Exception e) {
                e.printStackTrace();
                return -1L;
            }

            for (Route route : routes) {
                for (Leg leg : route.getLegs()) {
                    for (Step step : leg.getSteps()) {
                        dist += step.getDistance().getValue();
                    }
                }
            }

            if (routes.size() == 0) return -1L;

        }
        return dist;
    }

    public static long getTimeDistance(Context context, String json) {
        long time = 0;
        if (json != null) {
            Directions directions = new Directions(context);
            List<Route> routes;

            try {
                routes = directions.parse(json);
            } catch (Exception e) {
                e.printStackTrace();
                return -1L;
            }

            for (Route route : routes) {
                for (Leg leg : route.getLegs()) {
                    for (Step step : leg.getSteps()) {
                        time += step.getDuration().getValue();
                    }
                }
            }

            if (routes.size() == 0) return -1L;

        }
        return time;
    }



}
