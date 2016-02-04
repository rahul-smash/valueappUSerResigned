package com.signity.bonbon.Utilities;

public class FragmentAdapter {

//    public String[] text = {"Popular Items", "Food & Drinks", "Bath & Beauty", "Home & Hygiene", "Breakfast & Diary", "Baby Need", "Stables & Spices", "Special Collect"};
//    private static final String TAG = "MainActivity";
//    protected static String[] CONTENT = new String[10];
//    protected static final int[] ICONS = new int[]{
////            R.drawable.perm_group_calendar,
////            R.drawable.perm_group_camera,
////            R.drawable.perm_group_device_alarms,
////            R.drawable.perm_group_location
//    };
//
////    private int mCount = CONTENT.length;
//
//
//    FragmentManager fragmentManager;
//
//    public FragmentAdapter(FragmentManager fm) {
//        super(fm);
//        fragmentManager = fm;
//       /* CONTENT = new String[] {
//                "All Posts",
//            	"Calendar Events",
//            	"Gallery",
//            	"News",
//            	};*/
//
//        for (int i = 0; i < text.length; i++) {
//            CONTENT[i] = text[i];
//        }
//
//    }
//
//    //@Override
////public int getItemPosition(Object object) {
////	// TODO Auto-generated method stub
////	return 0;
////}
//    @Override
//    public Fragment getItem(int position) {
//
//        return MainActivity.testFragment.get(position);
////        return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
//    }
//
//    @Override
//    public int getCount() {
//        return MainActivity.testFragment.size();
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return FragmentAdapter.CONTENT[position % CONTENT.length];
////      return TestFragmentAdapter.CONTENT[position % CONTENT.length];
//    }
//
//    public int getIconResId(int index) {
//        return ICONS[index % ICONS.length];
//    }
//
//    public void setCount(int count) {
//        if (count > 0 && count <= 10) {
////            mCount = count;
//            notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public int getItemPosition(Object object) {
//        int index = MainActivity.testFragment.indexOf(object);
//        if (index != -1) {
//            Log.i(TAG, "Position: " + index);
//            return index;
//        } else {
//            Log.i(TAG, "POSITION_NONE");
//            return POSITION_NONE;
//        }
//    }
//
//    public void removeItem(int position) {
//        Fragment fragment = (Fragment) MainActivity.testFragment.get(position);
//        fragmentManager.beginTransaction().remove(fragment).commit();
//        MainActivity.testFragment.remove(position);
//        notifyDataSetChanged();
//
//    }
//
//    public void HideFragment(int position) {
//        Fragment fragment = (Fragment) MainActivity.testFragment.get(position);
//        /*fragmentManager.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//        .show(somefrag)
//        .commit();
//
//    	FragmentManager fm = getFragmentManager();*/
//        fragmentManager.beginTransaction()
//                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//                .hide(fragment)
//                .commit();
//    }


}