package pe.edu.pucp.gdp.Entity;

import java.io.Serializable;

public class RespuestaAPI implements Serializable {
    private String success;
    private String data;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
