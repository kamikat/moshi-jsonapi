package moe.banana.jsonapi;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Nullable;

public final class ResourceLinkages extends ResourceLinkage {

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

    public static final Parcelable.Creator<ResourceLinkages> CREATOR
            = new Parcelable.Creator<ResourceLinkages>() {
        public ResourceLinkages createFromParcel(Parcel in) {
            Parcelable[] parcelables = in.readParcelableArray(ResourceLinkages.class.getClassLoader());
            ResourceLinkages linkages = new ResourceLinkages();
            for (Parcelable parcelable : parcelables) {
                linkages.add((ResourceLinkage) parcelable);
            }
            return linkages;
        }

        public ResourceLinkages[] newArray(int size) {
            return new ResourceLinkages[size];
        }
    };

    @Override
    public boolean one() {
        return false;
    }

}
