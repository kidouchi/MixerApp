package kidouchi.mixerapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by iuy407 on 8/23/15.
 */
public class Sound implements Parcelable {

    private int _id;
    private String _soundFile;
//    private String _color;

//    public Sound(int id, String soundFile, String color) {
//        this._id = id;
//        this._soundFile = soundFile;
//        this._color = color;
//    }

    public Sound(int id, String soundFile) {
        this._id = id;
        this._soundFile = soundFile;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getSoundFilepath() {
        return _soundFile;
    }

    public void setSoundFilepath(String _soundFile) {
        this._soundFile = _soundFile;
    }

//    public String getColor() {
//        return _color;
//    }
//
//    public void setColor(String _color) {
//        this._color = _color;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(_soundFile);
//        dest.writeString(_color);
    }

    private Sound(Parcel in) {
        _id = in.readInt();
        _soundFile = in.readString();
//        _color = in.readString();
    }

    public static final Creator<Sound> CREATOR = new Creator<Sound>() {
        @Override
        public Sound createFromParcel(Parcel source) {
            return new Sound(source);
        }

        @Override
        public Sound[] newArray(int size) {
            return new Sound[size];
        }
    };
}
