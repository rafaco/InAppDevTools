package es.rafaco.devtools.tools.info;

import java.util.ArrayList;
import java.util.List;

public class InfoEntry {

    String label;
    List<String> values;

    public InfoEntry(String label, List<String> values) {
        this.label = label;
        this.values = values;
    }

    public InfoEntry(String label, String value) {
        this.label = label;
        setValue(value);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void setValue(String value) {
        this.values = new ArrayList<>();
        this.values.add(value);
    }

    public String toString() {
        String result = "";
        String formatWithLabel =    " - %s: %s";
        String formatWithoutLabel = "        %s";
        String lineBreak = "\n";

        List<String> values = getValues();
        if (values == null || values.size()<1){
            result += String.format(formatWithLabel, getLabel(), "Not available");
            result += lineBreak;
        }else{
            for (int i = 0; i<values.size(); i++){
                if(i==0){
                    result += String.format(formatWithLabel, getLabel(), values.get(0));
                    result += lineBreak;
                }else{
                    result += String.format(formatWithoutLabel, getLabel(), values.get(i));
                    result += lineBreak;
                }
            }
        }
        return result;
    }
}
