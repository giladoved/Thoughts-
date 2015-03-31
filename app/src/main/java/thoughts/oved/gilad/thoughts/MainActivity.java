package thoughts.oved.gilad.thoughts;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity {

    private static ArrayList<ParseObject> thoughtsArray;
    private static ArrayAdapter<ParseObject> arrayAdapter;
    private static int removedCount;
    static boolean isEmpty;

    EditText thoughtText;
    Button postThought;

    @InjectView(R.id.frame)
    SwipeFlingAdapterView flingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        removedCount = 0;
        isEmpty = true;
        thoughtsArray = new ArrayList<>();

        thoughtText = (EditText) findViewById(R.id.editText);
        postThought = (Button) findViewById(R.id.postBtn);
        postThought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject thoughtObject = new ParseObject("Thought");
                thoughtObject.put("thought", thoughtText.getText().toString().trim());
                thoughtObject.put("totalVotes", 0);
                thoughtObject.put("upVotes", 0);
                thoughtObject.put("rating", 0.0);
                thoughtObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        thoughtText.clearFocus();
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(thoughtText.getWindowToken(), 0);
                        thoughtText.setText("");
                        Toast.makeText(MainActivity.this, "Thought posted at this location", Toast.LENGTH_SHORT).show();
                        //maybe refresh not sure
                    }
                });
            }
        });

        arrayAdapter = new ObjectAdapter(this, R.layout.item, thoughtsArray);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                thoughtsArray.remove(0);
                if (++removedCount == thoughtsArray.size())
                    Toast.makeText(MainActivity.this, "No thoughts here...", Toast.LENGTH_SHORT).show();

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                ParseObject thought = (ParseObject)dataObject;
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Thought");
                query.getInBackground(thought.getObjectId(), new GetCallback<ParseObject>() {
                    public void done(ParseObject thoughtObject, ParseException e) {
                        if (e == null) {
                            int totalVotes = (int)thoughtObject.getNumber("totalVotes") + 1;
                            int upVotes = (int)thoughtObject.getNumber("upVotes");
                            double rating = upVotes/(double)totalVotes;
                            thoughtObject.put("totalVotes", totalVotes);
                            thoughtObject.put("rating", rating);
                        }
                    }
                });
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                ParseObject thought = (ParseObject)dataObject;
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Thought");
                query.getInBackground(thought.getObjectId(), new GetCallback<ParseObject>() {
                    public void done(ParseObject thoughtObject, ParseException e) {
                        if (e == null) {
                            int totalVotes = (int)thoughtObject.getNumber("totalVotes") + 1;
                            int upVotes = (int)thoughtObject.getNumber("upVotes") + 1;
                            double rating = upVotes/(double)totalVotes;
                            thoughtObject.put("totalVotes", totalVotes);
                            thoughtObject.put("upVotes", upVotes);
                            thoughtObject.put("rating", rating);
                            thoughtObject.saveInBackground();
                        }
                    }
                });
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here

            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeToast(MainActivity.this, "Clicked!"  + dataObject);
            }
        });
        //check for location first:

        //if found location, set arraylist to objects from parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Thought");
        query.addDescendingOrder("rating");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> thoughtsList, ParseException e) {
                if (e == null) {
                    thoughtsArray.clear();
                    for (ParseObject thought : thoughtsList) {
                        thoughtsArray.add(thought);
                        arrayAdapter.notifyDataSetChanged();
                    }
                    if (thoughtsArray.size() == 0)
                        Toast.makeText(MainActivity.this, "No thoughts here...", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

}
