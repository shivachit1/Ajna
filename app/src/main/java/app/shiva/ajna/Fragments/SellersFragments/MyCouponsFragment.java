package app.shiva.ajna.Fragments.SellersFragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import app.shiva.ajna.Adapter.SimpleCouponsAdapter;
import app.shiva.ajna.Model.SimpleCoupon;
import app.shiva.ajna.R;
import app.shiva.ajna.activities.SellersMapsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCouponsFragment extends Fragment {

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private SimpleCouponsAdapter simpleCouponsAdapter;
    public MyCouponsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myCouponsView = inflater.inflate(R.layout.fragment_my_coupons, container, false);
        ImageButton createCoupons = myCouponsView.findViewById(R.id.createCoupons);

        RecyclerView mycoupons=myCouponsView.findViewById(R.id.mycoupons);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mycoupons.setLayoutManager(horizontalLayoutManager);
        simpleCouponsAdapter = new SimpleCouponsAdapter(getContext(), getmyCoupons(), new SimpleCouponsAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(SimpleCoupon item) {
                Toast.makeText(getContext(), "Clicked Item : "+item.toString() , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void LongClick(SimpleCoupon item) {

            }
        });

        mycoupons.setAdapter(simpleCouponsAdapter);


        createCoupons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startThrowingCoupons(inflater);
            }
        });


        return myCouponsView;
    }

private ArrayList<Integer> couponsQuantity = new ArrayList<>();
    private EditText deadline;
    private String quantityValue,discount,deadlineValue,comment;
    private void startThrowingCoupons(final LayoutInflater inflater) {

        for(int i = 0; i < 100; i++) {
            couponsQuantity.add(i);
        }
        View couponsTemplateView = inflater.inflate(R.layout.couponstemplate, null);
        final Dialog dialogThrowingCoupons = new Dialog(getContext());
        Spinner mSpinner=couponsTemplateView.findViewById(R.id.couponsCounter);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinnersellerscategory, R.id.spinnervalue, couponsQuantity);
        mSpinner.setAdapter(adapter);
        final EditText discountPercent = couponsTemplateView.findViewById(R.id.discountPercent);
        deadline = couponsTemplateView.findViewById(R.id.deadline);
        final EditText mComment=couponsTemplateView.findViewById(R.id.comment);

        Button done=couponsTemplateView.findViewById(R.id.done);



       mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                quantityValue = parent.getItemAtPosition(position).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        deadline.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction())
                    selectDateTime(inflater); // Instead of your Toast
                return false;
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discount=discountPercent.getText().toString();
                deadlineValue=deadline.getText().toString();
                comment=mComment.getText().toString();

                String couponsId= mFirebaseDatabaseReference.child("SimpleCoupons").push().getKey();
                SimpleCoupon simpleCoupon=new SimpleCoupon(couponsId,userId,quantityValue,discount,deadlineValue,comment);
                mFirebaseDatabaseReference.child("SimpleCoupons").child(couponsId).setValue(simpleCoupon);
                Toast.makeText(getContext(), "Coupon Created : "+couponsId , Toast.LENGTH_SHORT).show();
                dialogThrowingCoupons.dismiss();

            }
        });



        dialogThrowingCoupons.setContentView(couponsTemplateView);
        dialogThrowingCoupons.show();


    }

    private int hour,minutes,day,mMonth,mYear;
    private void selectDateTime(LayoutInflater inflater) {

        View dateandTimepickerView = inflater.inflate(R.layout.dateandtimepicker, null);
        final Dialog dialogDatePickerView = new Dialog(getContext());
        dialogDatePickerView.setContentView(dateandTimepickerView);
        dialogDatePickerView.show();


        final TextView selectedTime = dateandTimepickerView.findViewById(R.id.selectedTime);

        // setting Text View Selected Time to current date and time for reference.
        //Any changes to the date and time can be previewed in textview.
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());

        Date date = localCalendar.getTime();
        day = localCalendar.get(Calendar.DATE);
        mMonth = localCalendar.get(Calendar.MONTH) + 1;
        mYear = localCalendar.get(Calendar.YEAR);

        hour = localCalendar.get(Calendar.HOUR_OF_DAY);
        minutes = localCalendar.get(Calendar.MINUTE);
        selectedTime.setText(mYear+"/"+mMonth+"/"+day+" "+hour+":"+minutes);


        CalendarView calendarView=dateandTimepickerView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                day = dayOfMonth;
                mMonth=month+1;
                mYear=year;
                selectedTime.setText(mYear+"/"+mMonth+"/"+day+" "+hour+":"+minutes);

            }
        });


        TimePicker simpleTimePicker =dateandTimepickerView.findViewById(R.id.timePicker1);
        simpleTimePicker.setIs24HourView(true);
        simpleTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // display a toast with changed values of time picker
                hour=hourOfDay;
                minutes=minute;
                Toast.makeText(getContext(), hourOfDay + "  " + minute, Toast.LENGTH_SHORT).show();
                selectedTime.setText(mYear+"/"+mMonth+"/"+day+" "+hour+":"+minutes); // set the current time in text view
            }
        });


        Button done = dateandTimepickerView.findViewById(R.id.doneSelectingTime);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deadline.setText(selectedTime.getText());
                dialogDatePickerView.dismiss();
            }
        });

    }

    ArrayList<SimpleCoupon> simpleCoupons= new ArrayList<>();
    public ArrayList<SimpleCoupon> getmyCoupons(){
        mFirebaseDatabaseReference.child("SimpleCoupons").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    simpleCoupons.clear();
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        String couponID=dataSnapshot1.child("couponId").getValue(String.class);
                        String couponCreaterId=dataSnapshot1.child("couponCreaterId").getValue(String.class);
                        String quantity=dataSnapshot1.child("quantity").getValue(String.class);
                        String discountPercentage=dataSnapshot1.child("discountPercentage").getValue(String.class);
                        String deadline=dataSnapshot1.child("deadline").getValue(String.class);
                        String comment=dataSnapshot1.child("comment").getValue(String.class);



                        SimpleCoupon simpleCoupon=new SimpleCoupon(couponID,couponCreaterId,quantity,discountPercentage,deadline,comment);
                        Toast.makeText(getContext(), simpleCoupon.getCouponId(), Toast.LENGTH_SHORT).show();
                        simpleCoupons.add(simpleCoupon);
                        simpleCouponsAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return simpleCoupons;
    }
}
