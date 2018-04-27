package moe.banana.jsonapi2;

public class ObjectDocument<DATA extends ResourceIdentifier> extends Document {

    private DATA data = null;
    private boolean nullFlag;

    public ObjectDocument() {
    }

    public ObjectDocument(Document document) {
        super(document);
    }

    public void set(DATA data) {
        bindDocument(null, this.data);
        bindDocument(this, data);
        this.data = data;
        this.nullFlag = data == null;
    }

    public DATA get() {
        return data;
    }

    @Deprecated
    public void setNull(boolean isNull) {
        nullFlag = isNull;
    }

    public boolean isNull() {
        return nullFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ObjectDocument<?> that = (ObjectDocument<?>) o;

        if (nullFlag != that.nullFlag) return false;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + data.hashCode();
        result = 31 * result + (nullFlag ? 1 : 0);
        return result;
    }
}
