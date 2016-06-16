package moe.banana.jsonapi;

import android.os.Parcel;
import android.os.Parcelable;

import android.support.annotation.Nullable;
import java.util.Map;

public final class Resources extends Resource {

    @Nullable
    @Override
    public Object attributes() {
        throw new InvalidAccessException();
    }

    @Nullable
    @Override
    public Map<String, Relationship> relationships() {
        throw new InvalidAccessException();
    }

    @Nullable
    @Override
    public Links links() {
        throw new InvalidAccessException();
    }

    @Override
    public String id() {
        throw new InvalidAccessException();
    }

    @Override
    public String type() {
        throw new InvalidAccessException();
    }

    @Nullable
    @Override
    public Object meta() {
        throw new InvalidAccessException();
    }

    @Override
    public String toString() {
        return toStringAsList();
    }

    @Override
    public int hashCode() {
        return hashCodeAsList();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return equalsAsList(o);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelableArray(toArray(new Parcelable[size()]), flags);
    }

    public static final Parcelable.Creator<Resources> CREATOR
            = new Parcelable.Creator<Resources>() {
        public Resources createFromParcel(Parcel in) {
            Parcelable[] parcelables = in.readParcelableArray(Resources.class.getClassLoader());
            Resources resources = new Resources();
            for (Parcelable parcelable : parcelables) {
                resources.add((Resources) parcelable);
            }
            return resources;
        }

        public Resources[] newArray(int size) {
            return new Resources[size];
        }
    };

    @Override
    public boolean one() {
        return false;
    }

}
