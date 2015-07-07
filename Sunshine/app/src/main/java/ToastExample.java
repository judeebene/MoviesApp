import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.android.sunshine.app.R;

/**
 * Created by ACER-PC on 5/22/2015.
 */
public class ToastExample extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.abc_search_view);

        Toast.makeText(this , " an example " ,Toast.LENGTH_LONG).show();
    }
}
