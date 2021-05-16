package Helpers;

import android.content.res.Resources;

import com.app.takemeaway.takemeaway.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalHelpers {
    /*public static String getString(String name) {
        return Resources.getSystem().getString(R.string.accountsController);
    }*/

    public static <T> T as(Class<T> clazz, Object o){
        if(clazz.isInstance(o)){
            return clazz.cast(o);
        }
        return null;
    }
}
