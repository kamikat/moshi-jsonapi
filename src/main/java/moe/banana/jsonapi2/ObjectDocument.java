package moe.banana.jsonapi2;

public class ObjectDocument<DATA extends ResourceIdentifier> extends Document<DATA> {

    DATA data = null;

    boolean isExplicitNull;

    public ObjectDocument() {
    }

    public ObjectDocument(Document<DATA> document) {
        super(document);
    }

    public void set(DATA data) {
        if (this.data != null) {
            this.data.setContext(null);
        }
        if (data != null) {
            data.setContext(this);
        }
        this.data = data;
    }

    public DATA get() {
        return data;
    }

    public void setNull(boolean isNull) {
        isExplicitNull = isNull;
    }

    public boolean isNull() {
        return isExplicitNull;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ObjectDocument<?> that = (ObjectDocument<?>) o;

        if (isExplicitNull != that.isExplicitNull) return false;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + data.hashCode();
        result = 31 * result + (isExplicitNull ? 1 : 0);
        return result;
    }
}
