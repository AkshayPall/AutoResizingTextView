# AutoResizingTextView
An Auto sizing TextView for Android that takes in a textsize attribute and uses it as a maximum text size (in SP) and scales down accordingly.

#Usage:

- by default, normal activity
- need to implement listener for instances where you do activate autofit
- to allow autofit, do .setAutoFit(true);. Can always disable this by setting it to false later on.
- This uses the initially set "textSize" attribute as the max text size and scales down if necessary. If you want to re-set this later on, use the .setMaxTextSize setter (this also redraws the view).
- similarly, can set the min text size && size changing increments
- scales in one direction to avoid showing user weird behaviour as the view redraws

Ex. having the tab titles in a bottom tab layout autofit

class SomeActivity implements AutoResizingTextView.AutoResizingTextViewListener
...
private void addTab(@StringRes int stringResourceId, @IdRes int id){
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        AutoResizingTextView customview = (AutoResizingTextView) view.findViewById(R.id.tabTxt);
        customview.setAutoFit(true);
        customview.setListener(this);
        view.setId(id);

        customview.setText(getString(stringResourceId));
        mTabLabelViews.add(customview);
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setCustomView(view);
        tabLayout.addTab(tab);
    }

private void setupTab() {
        mTabLabelViews = new ArrayList<>();
        for (int i = 0; i < 5; i++){
          addTab(R.string.a_message, R.string.an_id + i);  
        }
        setTabListner();
    }
...
@Override
    public void onAutoSizeComplete() {
        mTabSizingCount++;
        Log.d(TAG, mTabSizingCount + "tabs done sizing");
        if (mTabSizingCount == tabLayout.getTabCount()){
            Log.d(TAG, "unify text sizes across tabs");
            AutoResizingTextView.unifyTextSizeAcrossViews(mTabLabelViews,
                    getResources().getDisplayMetrics().scaledDensity);
        }
    }
...
