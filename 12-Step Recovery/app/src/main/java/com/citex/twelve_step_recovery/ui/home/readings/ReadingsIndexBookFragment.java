package com.citex.twelve_step_recovery.ui.home.readings;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.databinding.FragmentReadingsIndexBookBinding;
import com.citex.twelve_step_recovery.recyclerview.RecyclerViewAdapter;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReadingsIndexBookFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener {

    private ArrayList<ReadingsIndexBookModel> bookIndexCsv;
    private ArrayList<ReadingsIndexBookModel> aaBookForewordIndexCsv;
    private ArrayList<ReadingsIndexBookModel> naBookForewordIndexCsv;
    private String bookType = "standard";
    private static final String TAG = ReadingsIndexBookFragment.class.getName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Setup ActionBar
        if (getActivity() != null) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if (mActionBar != null) {

                // Initialize the custom action bar layout.
                LayoutInflater mInflater = LayoutInflater.from(getActivity());
                @SuppressLint("InflateParams") View mCustomView = mInflater.inflate(R.layout.fragment_readings_index_book_action_bar, null);
                mActionBar.setCustomView(mCustomView);
                mActionBar.setDisplayShowCustomEnabled(true);

                mActionBar.setIcon(R.drawable.ic_menu_book_24dp);
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);

                // Initialize action bar events.
                initActionBar(mActionBar);
            }
        }

        // Setup View Binding.
        FragmentReadingsIndexBookBinding binding = FragmentReadingsIndexBookBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        container.removeAllViews();

        // Create the RecyclerView.
        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_readings);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        ArrayList<String> readings = new ArrayList<>();
        bookIndexCsv = new ArrayList<>();
        aaBookForewordIndexCsv = new ArrayList<>();
        naBookForewordIndexCsv = new ArrayList<>();

        // Read index.
        try {
            String indexFilename = getArguments().getString("indexFilename");

            // Fragment has different options depending on book type.
            // AA has separate index for foreword in roman numerals
            if(indexFilename.contains("alcoholics-anonymous"))
                bookType = "alcoholics-anonymous";
            else if (indexFilename.contains("narcotics-anonymous"))
                bookType = "narcotics-anonymous";

            AssetManager assetManager = getActivity().getAssets();
            InputStream csvInputStream = assetManager.open(indexFilename);
            CSVReader reader = new CSVReaderBuilder(new InputStreamReader(csvInputStream))
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .withSkipLines(1)
                    .build();
            String[] nextLine;

            Integer count = 0;
            while ((nextLine = reader.readNext()) != null) {

                readings.add(nextLine[1]);

                ReadingsIndexBookModel readingsIndexBookModel = new ReadingsIndexBookModel();
                readingsIndexBookModel.Id = Integer.parseInt(nextLine[0]);
                readingsIndexBookModel.Title = nextLine[1];
                readingsIndexBookModel.Filename = nextLine[2];
                readingsIndexBookModel.PageStart = Integer.parseInt(nextLine[3]);
                readingsIndexBookModel.PageEnd = Integer.parseInt(nextLine[4]);
                readingsIndexBookModel.PageStartPdf = Integer.parseInt(nextLine[5]);
                readingsIndexBookModel.PageEndPdf = Integer.parseInt(nextLine[6]);
                readingsIndexBookModel.SplitBook = Boolean.parseBoolean(nextLine[7]);

                if(bookType.equals("alcoholics-anonymous")) {
                    if (count <= 8)
                        aaBookForewordIndexCsv.add(readingsIndexBookModel);
                    else
                        bookIndexCsv.add(readingsIndexBookModel);
                }
                else if(bookType.equals("narcotics-anonymous")) {
                    if (count <= 6)
                        naBookForewordIndexCsv.add(readingsIndexBookModel);
                    else
                        bookIndexCsv.add(readingsIndexBookModel);
                }
                else
                    bookIndexCsv.add(readingsIndexBookModel);

                count++;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        // Setup the RecyclerViewAdapter.
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(readings);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Setup the onClickListener.
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Add dividers between list items.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        return root;
    }

    @Override
    public void onItemClick(View view, int position) {

        // Find book contents page in array list.
        ReadingsIndexBookModel bookIndex = null;
        if(bookType.equals("alcoholics-anonymous")) {
            // Handle AA book foreword first 7 chapters.
            if(position <= 8) {
                for (int i = 0; i < aaBookForewordIndexCsv.size(); i++) {
                    if (i == position)
                        bookIndex = aaBookForewordIndexCsv.get(i);
                }
            }
            else {
                for (int i = 0; i < bookIndexCsv.size(); i++) {
                    if (i == position - 9)
                        bookIndex = bookIndexCsv.get(i);
                }
            }
        }
        else if(bookType.equals("narcotics-anonymous")) {
            // Handle NA book foreword first 6 chapters.
            if(position <= 6) {
                for (int i = 0; i < naBookForewordIndexCsv.size(); i++) {
                    if (i == position)
                        bookIndex = naBookForewordIndexCsv.get(i);
                }
            }
            else {
                for (int i = 0; i < bookIndexCsv.size(); i++) {
                    if (i == position - 7)
                        bookIndex = bookIndexCsv.get(i);
                }
            }
        }
        else {
            for (int i = 0; i < bookIndexCsv.size(); i++) {
                if (i == position)
                    bookIndex = bookIndexCsv.get(i);
            }
        }

        if (getActivity() != null) {

            if (bookIndex != null) {

                Bundle bundle = new Bundle();
                bundle.putString("title", "Book Viewer");
                bundle.putString("indexFilename", bookIndex.Filename);
                bundle.putInt("pageOffset", 0);

                // Create array of pages to open in PDF reader.
                if(bookIndex.SplitBook) {
                    int [] pages = new int[(bookIndex.PageEndPdf - bookIndex.PageStartPdf) + 1];
                    int c = 0;
                    for (int a = bookIndex.PageStartPdf- 1; a < bookIndex.PageEndPdf; a++) {
                        pages[c] = a;
                        c++;
                    }
                    bundle.putIntArray("pages", pages);
                }

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_readings_index_book_to_navigation_readings_content_pdf, bundle);
            }
        }
    }

    public void initActionBar(ActionBar actionBar) {

        // Go to page.
        TextView mapButton = actionBar.getCustomView().findViewById(R.id.go_to_page);
        mapButton.setOnClickListener(v -> {

            final AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();

            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_go_to_page, null);
            TextView title = customLayout.findViewById(R.id.txv_go_to_page_title);

            if(bookType.equals("alcoholics-anonymous")) {
                title.setText("Enter a page number:\r\n\r\np.i to p.xxxii\r\nor\r\np.1 to p.575");
            }
            else if(bookType.equals("narcotics-anonymous")) {
                title.setText("Enter a page number:\r\n\r\np.i to p.xxvi\r\nor\r\np.1 to p.426");
            }
            else {
                Integer pageStart = bookIndexCsv.get(0).PageStart;
                Integer pageEnd = bookIndexCsv.get(bookIndexCsv.size()-1).PageEnd;
                title.setText("Enter a page number:\r\n\r\np." + pageStart + " to p." + pageEnd);
            }

            EditText pageNumber = customLayout.findViewById(R.id.edt_enter_page_number);

            pageNumber.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    TextView invalidPageNumber = customLayout.findViewById(R.id.txv_invalid_page_number);
                    if(isValidPageNumber(s.toString()))
                        invalidPageNumber.setVisibility(View.GONE);
                    else
                        invalidPageNumber.setVisibility(View.VISIBLE);
                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }
            });

            dialog.setView(customLayout);

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText pageNumberEditText = customLayout.findViewById(R.id.edt_enter_page_number);
                            if(isValidPageNumber(pageNumberEditText.getText().toString())) {

                                dialog.dismiss();

                                String pageNumber = pageNumberEditText.getText().toString().toUpperCase(Locale.ROOT);
                                if(bookType.equals("alcoholics-anonymous") && isRomanNumeral(pageNumber)) {

                                    // Go to roman numeral page from AA book foreword.
                                    int number = romanToInteger(pageNumber);
                                    for (int i = 0; i < aaBookForewordIndexCsv.size(); i++) {
                                        ReadingsIndexBookModel bookIndex = aaBookForewordIndexCsv.get(i);
                                        if (number >= bookIndex.PageStart && number <= bookIndex.PageEnd) {

                                            Integer pageNumberOffset = number - bookIndex.PageStart;

                                            Bundle bundle = new Bundle();
                                            bundle.putString("title", "Book Viewer");
                                            bundle.putString("indexFilename", bookIndex.Filename);
                                            bundle.putInt("pageOffset", pageNumberOffset);

                                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                                            navController.navigate(R.id.action_navigation_readings_index_book_to_navigation_readings_content_pdf, bundle);
                                        }
                                    }
                                }
                                else if(bookType.equals("narcotics-anonymous") && isRomanNumeral(pageNumber)) {

                                    // Go to roman numeral page from AA book foreword.
                                    int number = romanToInteger(pageNumber);
                                    for (int i = 0; i < naBookForewordIndexCsv.size(); i++) {
                                        ReadingsIndexBookModel bookIndex = naBookForewordIndexCsv.get(i);
                                        if (number >= bookIndex.PageStart && number <= bookIndex.PageEnd) {

                                            Integer pageNumberOffset = number - bookIndex.PageStart;

                                            Bundle bundle = new Bundle();
                                            bundle.putString("title", "Book Viewer");
                                            bundle.putString("indexFilename", bookIndex.Filename);
                                            bundle.putInt("pageOffset", pageNumberOffset);

                                            // Specify pages to open in complete book.
                                            if(bookIndex.SplitBook) {
                                                int [] pages = new int[(bookIndex.PageEndPdf - bookIndex.PageStartPdf) + 1];
                                                int c = 0;
                                                for (int a = bookIndex.PageStartPdf - 1; a < bookIndex.PageEndPdf; a++) {
                                                    pages[c] = a;
                                                    c++;
                                                }
                                                bundle.putIntArray("pages", pages);
                                            }
                                                                                       
                                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                                            navController.navigate(R.id.action_navigation_readings_index_book_to_navigation_readings_content_pdf, bundle);
                                        }
                                    }
                                }
                                // Go to page.
                                else {
                                    int number = Integer.parseInt(pageNumber);
                                        for (int i = 0; i < bookIndexCsv.size(); i++) {

                                            ReadingsIndexBookModel bookIndex = bookIndexCsv.get(i);
                                            if(number >= bookIndex.PageStart && number <= bookIndex.PageEnd) {

                                                Integer pageNumberOffset = number - bookIndex.PageStart;
                                                Bundle bundle = new Bundle();
                                                bundle.putString("title", "Book Viewer");
                                                bundle.putString("indexFilename", bookIndex.Filename);
                                                bundle.putInt("pageOffset", pageNumberOffset);

                                                // Specify pages to open in complete book.
                                                if(bookIndex.SplitBook) {
                                                    int [] pages = new int[(bookIndex.PageEndPdf - bookIndex.PageStartPdf) + 1];
                                                    int c = 0;
                                                    for (int a = bookIndex.PageStartPdf - 1; a < bookIndex.PageEndPdf; a++) {
                                                        pages[c] = a;
                                                        c++;
                                                    }
                                                    bundle.putIntArray("pages", pages);
                                                }

                                                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                                                navController.navigate(R.id.action_navigation_readings_index_book_to_navigation_readings_content_pdf, bundle);
                                            }
                                        }
                                 }
                            }
                        }
                    });
                }
            });
            dialog.show();
        });
    }

    private boolean isNumeric(String input) {
        if (input == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(input);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean isRomanNumeral(String input) {
        return input.matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$");
    }

    public static int romanToInteger(String roman)
    {
        Map<Character,Integer> numbersMap = new HashMap<>();
        numbersMap.put('I',1);
        numbersMap.put('V',5);
        numbersMap.put('X',10);
        numbersMap.put('L',50);
        numbersMap.put('C',100);
        numbersMap.put('D',500);
        numbersMap.put('M',1000);

        int result=0;

        for(int i=0;i<roman.length();i++)
        {
            char ch = roman.charAt(i);      // Current Roman Character

            //Case 1
            if(i>0 && numbersMap.get(ch) > numbersMap.get(roman.charAt(i-1)))
            {
                result += numbersMap.get(ch) - 2*numbersMap.get(roman.charAt(i-1));
            }

            // Case 2: just add the corresponding number to result.
            else
                result += numbersMap.get(ch);
        }

        return result;
    }

    private boolean isValidPageNumber(String text) {

        if(text.equals("")) {
            return true;
        }

        // Check if valid roman numeral and in page range.
        if(bookType.equals("alcoholics-anonymous")) {
            if (isRomanNumeral(text.toUpperCase(Locale.ROOT))) {
                int number = romanToInteger(text.toUpperCase(Locale.ROOT));
                if (number >= 1 && number <= 32)
                    return true;
            }
        }
        else if(bookType.equals("narcotics-anonymous")) {
            if (isRomanNumeral(text.toUpperCase(Locale.ROOT))) {
                int number = romanToInteger(text.toUpperCase(Locale.ROOT));
                if (number >= 1 && number <= 26)
                    return true;
            }
        }

        // Check if valid page number in range.
        Integer pageStart = bookIndexCsv.get(0).PageStart;
        Integer pageEnd = bookIndexCsv.get(bookIndexCsv.size()-1).PageEnd;
        if(isNumeric(text)) {
            int number = Integer.parseInt(text);
            if(number >= pageStart && number <= pageEnd)
                return true;
        }

        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
