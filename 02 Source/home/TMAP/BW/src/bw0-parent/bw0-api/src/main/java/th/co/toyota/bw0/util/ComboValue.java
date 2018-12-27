package th.co.toyota.bw0.util;

public class ComboValue {
    private String stValue;
    private String stLabel;

    public ComboValue() {
    }

    public ComboValue(String stValue, String stLabel) {
        this.stLabel = stLabel;
        this.stValue = stValue;
    }

    public String getStValue() {
        return stValue;
    }

    public void setStValue(String stValue) {
        this.stValue = stValue;
    }

    public String getStLabel() {
        return stLabel;
    }

    public void setStLabel(String stLabel) {
        this.stLabel = stLabel;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ComboValue)) return false;
        ComboValue cv = (ComboValue)obj;
        if (cv.stLabel.equals(this.stLabel) && cv.stValue.equals(this.stValue)) return true;
        else return false;
    }
}
