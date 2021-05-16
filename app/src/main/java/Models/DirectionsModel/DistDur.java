package Models.DirectionsModel;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Google Api Distance and Duration model
/// </summary>
public class DistDur {
    /// <summary>
    /// Duration/Distance via text
    /// </summary>
    @SerializedName("text")
    public String Text;

    /// <summary>
    /// units
    /// </summary>
    @SerializedName("value")
    public long Value;

    public DistDur(String text, long value) {
        Text = text;
        Value = value;
    }
}
