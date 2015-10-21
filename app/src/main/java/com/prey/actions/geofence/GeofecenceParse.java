package com.prey.actions.geofence;

import android.content.Context;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.net.PreyHttpResponse;
import com.prey.net.PreyRestHttpClient;
import com.prey.net.PreyWebServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oso on 19-10-15.
 */
public class GeofecenceParse {

    public static List<Geofence>  getJSONFromUrl(Context ctx) {

        String comandos=null;
        try {
            comandos = PreyWebServices.getInstance().geofencing(ctx);
        }catch (Exception e){

        }
        return getJSONFromTxt(ctx, comandos);
    }

    public static  List<Geofence> getJSONFromTxt(Context ctx, String json) {


        json="{\"prey\":"+json+"}";



        //[{"id":2,"name":"oso","lat":"-34.2708359516","lng":"-71.1254882813","radius":0,"account_id":2,"direction":"in","state":null,"created_at":"2015-10-16T18:00:40.000Z","updated_at":"2015-10-16T18:00:40.000Z","color":"","expires":null}]

        List<Geofence> listGeofence=new ArrayList<Geofence>();

        PreyLogger.d(json);
        try{
            JSONObject jsnobject = new JSONObject(json);
            JSONArray jsonArray = jsnobject.getJSONArray("prey");
            for (int i = 0; i < jsonArray.length(); i++) {
                String jsonCommand= jsonArray.get(i).toString();
                JSONObject explrObject =new JSONObject(jsonCommand);
                PreyLogger.i(explrObject.toString());
                Geofence geofence=new Geofence();
                geofence.setId(explrObject.getString("id"));
                geofence.setName(explrObject.getString("id"));
                geofence.setLatitude(Double.parseDouble(explrObject.getString("lat")));
                geofence.setLongitude(Double.parseDouble(explrObject.getString("lng")));
                geofence.setRadius(Float.parseFloat(explrObject.getString("radius")));
                geofence.setType(explrObject.getString("type"));
                int expires=Integer.parseInt(explrObject.getString("expires"));
                if(expires>0){
                    geofence.setExpires(1000*expires);
                }else{
                    geofence.setExpires(expires);
                }
                listGeofence.add(geofence);
            }
        }catch(Exception e){
            PreyLogger.e("error in parser:"+e.getMessage(), e);
        }
        return listGeofence;
    }
}
