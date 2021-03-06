package wqyap762.rprqs;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wqyap762 on 14/04/16.
 */
public class OrderRequest extends StringRequest{
    private static final String ORDER_REQUEST_URL = "http://188.166.178.66/order_request.php";
    private Map<String, String> params;

    public OrderRequest(Double total_price, int quantity, String payment_status, String hpno, String menu_id, String ordered_on, String ready_on, Response.Listener<String> listener) {
        super(Request.Method.POST, ORDER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("total_price", String.valueOf(total_price));
        params.put("quantity", String.valueOf(quantity));
        params.put("payment_status", payment_status);
        params.put("hpno", hpno);
        params.put("menu_id", menu_id);
        params.put("ordered_on", ordered_on);
        params.put("ready_on", ready_on + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
