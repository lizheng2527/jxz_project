package com.zdhx.androidbase.ui.ykt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.account.HomeViewPagerAdapter;
import com.zdhx.androidbase.ui.treadssearch.UpFileActivity;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;
import com.zdhx.androidbase.view.pagerslidingtab.PagerSlidingTabStrip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.zdhx.androidbase.ui.MainActivity.setAllSelectText;
import static com.zdhx.androidbase.ui.ykt.BlackBoradAdapter.isMuchSelectMap;

/**
 * Created by lizheng on 2016/12/24.
 * 主页
 */

public class YKTFragment extends Fragment {

    private GridView gridView;

    private ArrayList<String> gridList;

    private ViewPager vp;

    private Context context;

    private View handWrite,balckBorad,teachResouse;

    private ListView handWriteLV,blackBoradLV,teachResouseLV;

    private ECProgressDialog dialog;

    public static ArrayList<TeacherEclassCourses> courses;
    public static StudentEclassCourses coursesForStudent;

    public static ArrayList<TweekVo> tweekVos;
    public static ArrayList<TweekVo> tweekVosForStudent;

    public void showDialog(boolean b,String title){
        if (b){
            dialog.setPressText(title);
            dialog.show();
        }else{
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        dialog = new ECProgressDialog(context);
        if (ECApplication.getInstance().getUserForYKT() != null){
            initGridView();
            initViewPager();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initGridView() {
        gridView = (GridView) getView().findViewById(R.id.fragment_ykt_grid);
        gridList = new ArrayList();
        gridList.add("手写笔记");
        gridList.add("板书记录");
        gridList.add("教学课件");
    }
    private List<View> viewPagerListDatas;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private HomeViewPagerAdapter homeViewPagerAdapter;
    private HandWriteAdapter handWriteAdapter;
    private StudentHandWriteAdapter studentHandWriteAdapter;
    //    private ArrayList<HandWriteBean> handWriteList;
    private BlackBoradAdapter blackBoradAdapter;
    private ArrayList<BlackboardWrite> blackBoradList;
    private ArrayList<BlackboardWrite> blackBoradListNow;
    private TeachCourseAdapter teachCourseAdapter;
    private ArrayList<CourseWare> teachResouseList;
    private ArrayList<CourseWare> teachResouseListNow;
    private ArrayList<StudentWriteAnswerResults>studentHandWriteList;
    public static RecentDataConditions condition;
    public static RecentDataConditions conditionForStudent;

    private void onCancelMuchSelect(){
        if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
            BlackBoradAdapter.isMuchSelect = false;
            BlackBoradAdapter.isMuchSelectMap.clear();
            MainActivity.setAllSelectText(false);
            MainActivity.showSelectBatchLinear(false);
            notifyForMuchSelect();
        }
    }


    private void initViewPager(){
        vp = (ViewPager) getView().findViewById(R.id.fragment_ykt_viewpager);
        handWrite = LayoutInflater.from(context).inflate(R.layout.fragment_ykt_viewpager_handwrite,null);
        handWriteLV = (ListView) handWrite.findViewById(R.id.fragment_ykt_viewpager_handwrite_listview);
        balckBorad = LayoutInflater.from(context).inflate(R.layout.fragment_ykt_viewpager_blackborad,null);
        blackBoradLV = (ListView) balckBorad.findViewById(R.id.fragment_ykt_viewpager_blackborad_listview);
        teachResouse = LayoutInflater.from(context).inflate(R.layout.fragment_ykt_viewpager_teachresouse,null);
        teachResouseLV = (ListView) teachResouse.findViewById(R.id.fragment_ykt_viewpager_teachresouse_listview);

        viewPagerListDatas = new ArrayList<>();
        viewPagerListDatas.add(handWrite);
        viewPagerListDatas.add(balckBorad);
        viewPagerListDatas.add(teachResouse);


//        handWriteList = new ArrayList<>();
        blackBoradList = new ArrayList<>();
        blackBoradListNow = new ArrayList<>();

        teachResouseList = new ArrayList<>();
        teachResouseListNow = new ArrayList<>();

        mPagerSlidingTabStrip = (PagerSlidingTabStrip)getView().findViewById(R.id.tabs);
        vp.setOffscreenPageLimit(1);
        homeViewPagerAdapter = new HomeViewPagerAdapter(viewPagerListDatas,gridList);
        vp.setAdapter(homeViewPagerAdapter);
        mPagerSlidingTabStrip.setShouldExpand(true);
        mPagerSlidingTabStrip.setViewPager(vp,null,null);
        mPagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#4cbbda"));
        mPagerSlidingTabStrip.setTabPaddingLeftRight(10);
        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadYKTDatas(position);
                onCancelMuchSelect();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPagerSlidingTabStrip.setOnTabClickListener(new PagerSlidingTabStrip.OnTabClickListener() {
            @Override
            public void onTabClick(View tab, int position) {
                showDialog(true,"正在获取..");
                YKTPOSITION = position;
                if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
                    getHandWriteForTeacher(position);
                }else{
                    getHandWriteForStudent(position);
                }
                onCancelMuchSelect();
            }
        });
        //学生登录
        if (ECApplication.getInstance().getUserForYKT() != null && ECApplication.getInstance().getUserForYKT().getType().equals("0")){
            studentHandWriteList = new ArrayList<>();
            new ProgressThreadWrap(context, new RunnableWrap() {
                @Override
                public void run() {
                    HashMap<String,ParameterValue> map = new HashMap<String, ParameterValue>();
                    map.putAll(ECApplication.getInstance().getLoginUrlMap());
                    map.put("studentId",new ParameterValue(ECApplication.getInstance().getUserForYKT().getId()));
                    try {
                        //获取周数据
                        String json2 = ZddcUtil.getTweekVoJson(ECApplication.getInstance().getLoginUrlMap());
                        //解析周数据并存储到集合中
                        tweekVosForStudent = new Gson().fromJson(json2, new TypeToken<List<TweekVo>>(){}.getType());
                        if (tweekVosForStudent == null ||tweekVosForStudent.size() == 0){
                            ToastUtil.showMessage("教学周存在错误!");
                            return;
                        }
                        //获取学生的课程(如果没课程，结束)
                        String studentEclassCoursesJson = ZddcUtil.getStudentEclassCourseJson(map);
                        coursesForStudent = new Gson().fromJson(studentEclassCoursesJson,StudentEclassCourses.class);
//                        //如果学生没有课程，结束
                        if (coursesForStudent == null){
                            ToastUtil.showMessage("无课程！");
                            coursesForStudent = new StudentEclassCourses();
                            return;
                        }
                        //获取最近有数据的条件
                        String json1 = ZddcUtil.getStudentRecentDataConditionsJson(map);
                        //解析最近有数据的json
                        conditionForStudent = new Gson().fromJson(json1,RecentDataConditions.class);
                        if (conditionForStudent != null && conditionForStudent.getCourseId() != null &&conditionForStudent.getWeekNumber() != null){
                            //根据有数据的条件获取手写笔记
                            HashMap<String,ParameterValue> hashMap = new HashMap<String, ParameterValue>();
                            hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                            hashMap.put("studentId",new ParameterValue(ECApplication.getInstance().getUserForYKT().getId()));
                            eclassId = coursesForStudent.getEclass().getId();
                            hashMap.put("eclassId",new ParameterValue(eclassId));
                            courseId = conditionForStudent.getCourseId();
                            hashMap.put("courseId",new ParameterValue(courseId));
                            startDate = (tweekVosForStudent.get(Integer.parseInt(conditionForStudent.getWeekNumber())-1)).getStartDate();
                            hashMap.put("startDate",new ParameterValue(startDate));
                            endDate = (tweekVosForStudent.get(Integer.parseInt(conditionForStudent.getWeekNumber())-1)).getEndDate();
                            hashMap.put("endDate",new ParameterValue(endDate));
                            //将本次选择的周存入到搜索条件集合中
                            SearchHandWriteActivity.selectWeekMap.put("true",Integer.parseInt(conditionForStudent.getWeekNumber())-1);
                            //学生获取当前学期课堂集合
                            String handWriteJson = ZddcUtil.getWisdomClassesJsonWithStudent(hashMap);
                            ArrayList<WisdomClassesJsonWithTeacher> jsonWithStudents = new Gson().fromJson(handWriteJson, new TypeToken<List<WisdomClassesJsonWithTeacher>>(){}.getType());
                            HashMap<String,ParameterValue> map1 = new HashMap();
                            map1.putAll(ECApplication.getInstance().getLoginUrlMap());
                            map1.put("studentId",new ParameterValue(ECApplication.getInstance().getUserForYKT().getId()));
                            StringBuffer sb1 = new StringBuffer();
                            HashMap<String,WisdomClassesJsonWithTeacher> sexMaps = new HashMap<String, WisdomClassesJsonWithTeacher>();
                            //拼接课程ID（“，”隔开）
                            for (int i = 0; i < jsonWithStudents.size(); i++) {
                                if (i == jsonWithStudents.size()-1){
                                    sb1.append(jsonWithStudents.get(i).getId());
                                }else{
                                    sb1.append(jsonWithStudents.get(i).getId()+",");
                                }
                                sexMaps.put(jsonWithStudents.get(i).getId(),jsonWithStudents.get(i));
                            }
                            map1.put("wisdomClassId",new ParameterValue(sb1.toString()));
                            String json = ZddcUtil.getWriteAnswerResultsJsonWithStudentAndClass(map1);
                            ArrayList<StudentWriteAnswerResults> answerResults = new Gson().fromJson(json, new TypeToken<List<StudentWriteAnswerResults>>(){}.getType());


                            if (jsonWithStudents != null&&jsonWithStudents.size()>0){
                                //获取显示名称（学科）
                                String name = "";
                                for (int i = 0; i < coursesForStudent.getCourses().size(); i++) {
                                    if (coursesForStudent.getCourses().get(i).getId().equals(courseId)){
                                        name = coursesForStudent.getCourses().get(i).getName();
                                        SearchHandWriteActivity.selectBooksMap.put("true",i);
                                        break;
                                    }
                                }
                                if (answerResults != null){
                                    for (int i = 0; i < answerResults.size(); i++) {
                                        StudentWriteAnswerResults bean = new StudentWriteAnswerResults();
                                        //设置名称
                                        String nowName = sexMaps.get(answerResults.get(i).getWisdomClassId()).getName();
                                        Log.w("YKT",nowName);
                                        if (nowName != null && !("临时课程").equals(nowName)){
                                            String[] strs = nowName.split(" ");
                                            StringBuffer sb = new StringBuffer();
                                            String time = answerResults.get(i).getStartTime();
                                            for (int i2 = 0; i2 < strs.length; i2++) {
                                                if (i2 != 0){
                                                    sb.append(strs[i2]+"  ");
                                                }else{
                                                    time = strs[0]+"   "+time;
                                                }
                                            }
                                            bean.setTitle(sb.toString()+" "+name+"handWrite"+i);
                                            bean.setStartTime(time);
                                        }else{
                                            bean.setTitle(name+"handWrite"+i);
                                            bean.setStartTime(nowName+" "+answerResults.get(i).getStartTime());
                                        }
                                        bean.setQuestionName(bean.getTitle());
                                        bean.setTeacherName(sexMaps.get(answerResults.get(i).getWisdomClassId()).getTeacehrName());
                                        bean.setSex(sexMaps.get(answerResults.get(i).getWisdomClassId()).getSex());
                                        bean.setPreviewImage(answerResults.get(i).getPreviewImage());
                                        bean.setPreviewImageAttachmentId(answerResults.get(i).getPreviewImageAttachmentId());
                                        bean.setOriginalImage(answerResults.get(i).getOriginalImage());
                                        bean.setOriginalImageAttachmentId(answerResults.get(i).getOriginalImageAttachmentId());
                                        bean.setAnswers(answerResults.get(i).getAnswers());
                                        if (answerResults.get(i).getPreviewImage() == null&&answerResults.get(i).getOriginalImage() == null){
                                            if (answerResults.get(i).getAnswers() == null||answerResults.get(i).getAnswers().size()== 0){
                                                Log.w("YKT",answerResults.get(i).getWisdomClassId()+"条数据没有预览图");
                                            }else{
                                                studentHandWriteList.add(bean);
                                            }
                                        }else{
                                            studentHandWriteList.add(bean);
                                        }
                                    }
                                }
                                for (int i = 0; i < jsonWithStudents.size(); i++) {
                                    if (jsonWithStudents.get(i).getBlackboardWrite() != null&&jsonWithStudents.get(i).getBlackboardWrite().size()>0){
                                        //封装板书记录的集合
                                        for (int i1 = 0; i1 < jsonWithStudents.get(i).getBlackboardWrite().size(); i1++) {
                                            BlackboardWrite bean = new BlackboardWrite();
                                            String str = jsonWithStudents.get(i).getName();
                                            String[] strs = str.split(" ");
                                            StringBuffer sb = new StringBuffer();
                                            for (int i2 = 0; i2 < strs.length; i2++) {
                                                if ("临时课程".equals(strs[i2])){
                                                    sb.append(strs[i2]+" ");
                                                    break;
                                                }else{
                                                    if (i2 != 0){
                                                        sb.append(strs[i2]+" ");
                                                    }
                                                }
                                            }
                                            bean.setOriginalImageAttachmentId( jsonWithStudents.get(i).getBlackboardWrite().get(i1).getOriginalImageAttachmentId());
                                            bean.setPreviewImageAttachmentId( jsonWithStudents.get(i).getBlackboardWrite().get(i1).getPreviewImageAttachmentId());
                                            bean.setTeacherName(jsonWithStudents.get(i).getTeacehrName());
                                            bean.setName(sb.toString()+name+"blackBorad"+(i1+1)+".jpg");
                                            bean.setDateTime(jsonWithStudents.get(i).getDateTime());
                                            bean.setPreviewImage(jsonWithStudents.get(i).getBlackboardWrite().get(i1).getPreviewImage());
                                            bean.setOriginalImage(jsonWithStudents.get(i).getBlackboardWrite().get(i1).getOriginalImage());
                                            blackBoradList.add(bean);
                                        }

                                        //封装教学课件
                                        for (int i1 = 0; i1 < jsonWithStudents.get(i).getCourseware().size(); i1++) {
                                            CourseWare c = new CourseWare();
                                            String str = jsonWithStudents.get(i).getName();
                                            String[] strs = str.split(" ");
                                            StringBuffer sb = new StringBuffer();
                                            for (int i2 = 0; i2 < strs.length; i2++) {
                                                if ("临时课程".equals(strs[i2])){
                                                    sb.append(strs[i2]+" ");
                                                    break;
                                                }else{
                                                    if (i2 != 0){
                                                        sb.append(strs[i2]+" ");
                                                    }
                                                }
                                            }
                                            c.setTeacherName(jsonWithStudents.get(i).getTeacehrName());
                                            c.setTitle(sb.toString()+name);
                                            c.setSex(jsonWithStudents.get(i).getSex());
                                            c.setDateTime(jsonWithStudents.get(i).getDateTime());
                                            c.setFileName(jsonWithStudents.get(i).getCourseware().get(i1).getName());
                                            c.setUrl(jsonWithStudents.get(i).getCourseware().get(i1).getUrl());
                                            teachResouseList.add(c);
                                        }
                                    }
                                }
                            }

                        }else{
                            HashMap<String,ParameterValue> hashMap = new HashMap<String, ParameterValue>();
                            hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                            hashMap.put("studentId",new ParameterValue(ECApplication.getInstance().getUserForYKT().getId()));
                            eclassId = coursesForStudent.getEclass().getId();
                            hashMap.put("eclassId",new ParameterValue(eclassId));
                            if (coursesForStudent.getCourses().size()>0){
                                courseId = coursesForStudent.getCourses().get(0).getId();
                                hashMap.put("courseId",new ParameterValue(courseId));
                            }
                            startDate = (tweekVosForStudent.get(0)).getStartDate();
                            hashMap.put("startDate",new ParameterValue(startDate));
                            endDate = (tweekVosForStudent.get(0)).getEndDate();
                            hashMap.put("endDate",new ParameterValue(endDate));
                            //将本次选择的周存入到搜索条件集合中
                            SearchHandWriteActivity.selectWeekMap.put("true",0);
                            ToastUtil.showMessage("云课堂无数据..");
                        }

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                studentHandWriteAdapter = new StudentHandWriteAdapter(studentHandWriteList,context);
                                handWriteLV.setAdapter(studentHandWriteAdapter);
                                handWriteLV.setEmptyView(handWrite.findViewById(R.id.handwriteempty));
                            }
                        },5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        //教师端登录
        if (ECApplication.getInstance().getUserForYKT() != null && ECApplication.getInstance().getUserForYKT().getType().equals("2")){//教师
            onTeacherLogin();
        }
    }

    private void onTeacherLogin(){
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
                    HashMap<String,ParameterValue> map = new HashMap<String, ParameterValue>();
                    map.putAll(ECApplication.getInstance().getLoginUrlMap());
                    map.put("teacherId",new ParameterValue(ECApplication.getInstance().getUserForYKT().getId()));
                    try {
                        //获取周数据
                        String json2 = ZddcUtil.getTweekVoJson(ECApplication.getInstance().getLoginUrlMap());
                        //解析周数据并存储到集合中
                        tweekVos = new Gson().fromJson(json2, new TypeToken<List<TweekVo>>(){}.getType());
                        if (tweekVos == null||tweekVos.size() == 0){
                            ToastUtil.showMessage("教学周存在错误!");
                            return;
                        }
                        //获取最近有数据的条件
                        String json1 = ZddcUtil.getTeacherRecentDataConditionsJson(map);
                        //解析最近有数据的json
                        condition = new Gson().fromJson(json1,RecentDataConditions.class);
                        //获取该教师的课程
                        String TeacherEclassCoursesJson = ZddcUtil.getTeacherEclassCoursesJson(map);
                        courses = new Gson().fromJson(TeacherEclassCoursesJson, new TypeToken<List<TeacherEclassCourses>>(){}.getType());
                        //如果该教师没有课程，结束
                        if (courses == null||courses.size() == 0){
                            ToastUtil.showMessage("该教师无课程!");
                            return;
                        }

                        //根据有数据的条件获取手写笔记
                        HashMap<String,ParameterValue> hashMap = new HashMap<String, ParameterValue>();
                        hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                        hashMap.put("teacherId",new ParameterValue(ECApplication.getInstance().getUserForYKT().getId()));

                        if (condition != null && condition.getEclassId()!= null){
                            eclassId = condition.getEclassId();
                        }else{
                            eclassId = courses.get(0).getId();
                        }

                        hashMap.put("eclassId",new ParameterValue(eclassId));
                        if (condition != null && condition.getCourseId()!= null){
                            courseId = condition.getCourseId();
                        }else{
                            courseId = courses.get(0).getCourses().get(0).getId();
                        }
                        hashMap.put("courseId",new ParameterValue(courseId));

                        if (condition != null && condition.getWeekNumber()!= null){
                            int weekNums = Integer.parseInt(condition.getWeekNumber())-1;
                            if (weekNums>tweekVos.size()){
                                startDate = (tweekVos.get(tweekVos.size()-1)).getStartDate();
                                endDate = (tweekVos.get(tweekVos.size()-1)).getEndDate();
                            }else{
                                startDate = (tweekVos.get(weekNums)).getStartDate();
                                endDate = (tweekVos.get(weekNums)).getEndDate();
                            }
                            //将本次选择的周存入到搜索条件集合中
                            SearchHandWriteActivity.selectWeekMap.put("true",Integer.parseInt(condition.getWeekNumber())-1);
                        }else{
                            startDate = (tweekVos.get(0)).getStartDate();
                            endDate = (tweekVos.get(0)).getEndDate();
                            //将本次选择的周存入到搜索条件集合中
                            SearchHandWriteActivity.selectWeekMap.put("true",0);
                        }
                        hashMap.put("startDate",new ParameterValue(startDate));
                        hashMap.put("endDate",new ParameterValue(endDate));

                        String handWriteJson = ZddcUtil.getWisdomClassesJsonWithTeacher(hashMap);
                        ArrayList<WisdomClassesJsonWithTeacher> jsonWithTeachers = new Gson().fromJson(handWriteJson, new TypeToken<List<WisdomClassesJsonWithTeacher>>(){}.getType());
                        if (jsonWithTeachers == null || jsonWithTeachers.size() == 0){
                            return;
                        }
                        //获取显示名称（学科）
                        String name = "";
                        for (int i = 0; i < courses.size(); i++) {
                            for (int i1 = 0; i1 < courses.get(i).getCourses().size(); i1++) {
                                if (condition.getCourseId().equals(courses.get(i).getCourses().get(i1).getId())){
                                    name = courses.get(i).getCourses().get(i1).getName();
                                    SearchHandWriteActivity.selectEclassMap.put("true",i);
                                    break;
                                }
                            }
                            break;
                        }
                        int count = 0;
                        for (int i = 0; i < jsonWithTeachers.size(); i++) {

                            //封装即将展示手写笔记的集合
                            for (int i1 = 0; i1 < jsonWithTeachers.get(i).getQuestions2().size(); i1++) {
                                count++;
                                HandWriteBean bean = new HandWriteBean();
                                bean.setQuestionId(jsonWithTeachers.get(i).getQuestions2().get(i1).getId());
                                String questionName = jsonWithTeachers.get(i).getName()+" "+name+" question"+count;
                                Log.w("YKT:i1 = ",i1+"");
                                String time = jsonWithTeachers.get(i).getQuestions2().get(i1).getStartTime();
                                String[] strs = questionName.split(" ");
                                StringBuffer sb = new StringBuffer();
                                for (int i2 = 0; i2 < strs.length; i2++) {
                                    if (i2 != 0){
                                        sb.append(strs[i2]+"  ");
                                    }else{
                                        time = strs[0]+"   "+time;
                                    }

                                }
                                bean.setPreviewImageAttachmentQuestionId(jsonWithTeachers.get(i).getQuestions2().get(i1).getPreviewImageAttachmentId());
                                bean.setOriginalImageAttachmentQuestionId(jsonWithTeachers.get(i).getQuestions2().get(i1).getOriginalImageAttachmentId());
                                bean.setSex(jsonWithTeachers.get(i).getSex());
                                bean.setQuestionName(sb.toString());
                                bean.setDateTime(time);
                                bean.setPreviewQuestionImg(jsonWithTeachers.get(i).getQuestions2().get(i1).getPreviewImage());
                                bean.setOriginaQuestionlImg(jsonWithTeachers.get(i).getQuestions2().get(i1).getOriginalImage());
                                handWriteBeans.add(bean);
                            }
                            //封装板书记录的集合
                            for (int i1 = 0; i1 < jsonWithTeachers.get(i).getBlackboardWrite().size(); i1++) {
                                BlackboardWrite bean = new BlackboardWrite();
                                String str = jsonWithTeachers.get(i).getName();
                                String[] strs = str.split(" ");
                                StringBuffer sb = new StringBuffer();
                                for (int i2 = 0; i2 < strs.length; i2++) {
                                    if ("临时课程".equals(strs[i2])){
                                        sb.append(strs[i2]+" ");
                                        break;
                                    }else{
                                        if (i2 != 0){
                                            sb.append(strs[i2]+" ");
                                        }
                                    }
                                }
                                bean.setOriginalImageAttachmentId(jsonWithTeachers.get(i).getBlackboardWrite().get(i1).getOriginalImageAttachmentId());
                                bean.setPreviewImageAttachmentId(jsonWithTeachers.get(i).getBlackboardWrite().get(i1).getPreviewImageAttachmentId());
                                bean.setName(sb.toString()+name+"blackBorad"+(i1+1)+".jpg");
                                bean.setDateTime(jsonWithTeachers.get(i).getDateTime());
                                bean.setTeacherName(ECApplication.getInstance().getUserForYKT().getName());
                                bean.setPreviewImage(jsonWithTeachers.get(i).getBlackboardWrite().get(i1).getPreviewImage());
                                bean.setOriginalImage(jsonWithTeachers.get(i).getBlackboardWrite().get(i1).getOriginalImage());
                                blackBoradList.add(bean);
                            }
                            //封装教学课件
                            for (int i1 = 0; i1 < jsonWithTeachers.get(i).getCourseware().size(); i1++) {
                                CourseWare c = new CourseWare();
                                String str = jsonWithTeachers.get(i).getName();
                                String[] strs = str.split(" ");
                                StringBuffer sb = new StringBuffer();

                                for (int i2 = 0; i2 < strs.length; i2++) {
                                    if ("临时课程".equals(strs[i2])){
                                        sb.append(strs[i2]+" ");
                                        break;
                                    }else{
                                        if (i2 != 0){
                                            sb.append(strs[i2]+" ");
                                        }
                                    }
                                }
                                c.setAttachmentId(jsonWithTeachers.get(i).getCourseware().get(i1).getAttachmentId());
                                c.setSex(jsonWithTeachers.get(i).getSex());
                                c.setTitle(sb.toString()+name);
                                c.setDateTime(jsonWithTeachers.get(i).getDateTime());
                                c.setFileName(jsonWithTeachers.get(i).getCourseware().get(i1).getName());
                                c.setUrl(jsonWithTeachers.get(i).getCourseware().get(i1).getUrl());
                                teachResouseList.add(c);
                            }
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handWriteAdapter = new HandWriteAdapter(handWriteBeans,context);
                                handWriteLV.setAdapter(handWriteAdapter);
                                handWriteLV.setEmptyView(handWrite.findViewById(R.id.handwriteempty));
                            }
                        },5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private String eclassId ;
    private String courseId ;
    private String startDate ;
    private String endDate ;

    public String getEclassId() {
        return eclassId;
    }

    public void setEclassId(String eclassId) {
        this.eclassId = eclassId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * 学生根据条件获取课程数据
     * @param searchPosition
     */
    public void getHandWriteForStudent(final int searchPosition){
        switch (searchPosition){
            case 0:
                if (isLoading1){
                    showDialog(false,null);
                    return;
                }else{
                    setLoading1(true);
                }
                break;
            case 1:
                if (isLoading1){
                    showDialog(false,null);
                    return;
                }else{
                    setLoading2(true);
                }
                break;
            case 2:
                if (isLoading2){
                    showDialog(false,null);
                    return;
                }else{
                    setLoading3(true);
                }
                break;
        }

        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                studentHandWriteList.clear();
                blackBoradList.clear();
                teachResouseList.clear();
                try {
                    //根据有数据的条件获取手写笔记
                    HashMap<String,ParameterValue> hashMap = new HashMap<String, ParameterValue>();
                    hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                    hashMap.put("studentId",new ParameterValue(ECApplication.getInstance().getUserForYKT().getId()));
                    hashMap.put("eclassId",new ParameterValue(eclassId));
                    hashMap.put("courseId",new ParameterValue(courseId));
                    hashMap.put("startDate",new ParameterValue(startDate));
                    hashMap.put("endDate",new ParameterValue(endDate));
                    //学生获取当前学期课堂集合
                    String handWriteJson = ZddcUtil.getWisdomClassesJsonWithStudent(hashMap);
                    ArrayList<WisdomClassesJsonWithTeacher> jsonWithStudents = new Gson().fromJson(handWriteJson, new TypeToken<List<WisdomClassesJsonWithTeacher>>(){}.getType());
                    HashMap<String,ParameterValue> map1 = new HashMap();
                    map1.putAll(ECApplication.getInstance().getLoginUrlMap());
                    map1.put("studentId",new ParameterValue(ECApplication.getInstance().getUserForYKT().getId()));
                    StringBuffer sb1 = new StringBuffer();
                    HashMap<String,WisdomClassesJsonWithTeacher> sexMaps = new HashMap<String, WisdomClassesJsonWithTeacher>();
                    //拼接课程ID（“，”隔开）
                    for (int i = 0; i < jsonWithStudents.size(); i++) {
                        if (i == jsonWithStudents.size()-1){
                            sb1.append(jsonWithStudents.get(i).getId());
                        }else{
                            sb1.append(jsonWithStudents.get(i).getId()+",");
                        }
                        sexMaps.put(jsonWithStudents.get(i).getId(),jsonWithStudents.get(i));
                    }
                    map1.put("wisdomClassId",new ParameterValue(sb1.toString()));
                    String json = ZddcUtil.getWriteAnswerResultsJsonWithStudentAndClass(map1);
                    ArrayList<StudentWriteAnswerResults> answerResults = new Gson().fromJson(json, new TypeToken<List<StudentWriteAnswerResults>>(){}.getType());
                    if (jsonWithStudents != null&&jsonWithStudents.size()>0){
                        //获取显示名称（学科）
                        String name = "";
                        for (int i = 0; i < coursesForStudent.getCourses().size(); i++) {
                            if (coursesForStudent.getCourses().get(i).getId().equals(courseId)){
                                name = coursesForStudent.getCourses().get(i).getName();
                                SearchHandWriteActivity.selectBooksMap.put("true",i);
                                break;
                            }
                        }
                        if (answerResults != null){
                            for (int i = 0; i < answerResults.size(); i++) {
                                StudentWriteAnswerResults bean = new StudentWriteAnswerResults();
                                //设置名称
                                String nowName = sexMaps.get(answerResults.get(i).getWisdomClassId()).getName();
                                Log.w("YKT",nowName);
                                if (nowName != null && !("临时课程").equals(nowName)){
                                    String[] strs = nowName.split(" ");
                                    StringBuffer sb = new StringBuffer();
                                    String time = answerResults.get(i).getStartTime();
                                    for (int i2 = 0; i2 < strs.length; i2++) {
                                        if (i2 != 0){
                                            sb.append(strs[i2]+"  ");
                                        }else{
                                            time = strs[0]+"   "+time;
                                        }
                                    }
                                    bean.setTitle(sb.toString()+" "+name+"handWrite"+i);
                                    bean.setStartTime(time);
                                }else{
                                    bean.setTitle(name+"handWrite"+i);
                                    bean.setStartTime(nowName+" "+answerResults.get(i).getStartTime());
                                }
                                bean.setQuestionName(bean.getTitle());
                                bean.setTeacherName(sexMaps.get(answerResults.get(i).getWisdomClassId()).getTeacehrName());
                                bean.setSex(sexMaps.get(answerResults.get(i).getWisdomClassId()).getSex());
                                bean.setPreviewImage(answerResults.get(i).getPreviewImage());
                                bean.setPreviewImageAttachmentId(answerResults.get(i).getPreviewImageAttachmentId());
                                bean.setOriginalImage(answerResults.get(i).getOriginalImage());
                                bean.setOriginalImageAttachmentId(answerResults.get(i).getOriginalImageAttachmentId());
                                bean.setAnswers(answerResults.get(i).getAnswers());
                                if (answerResults.get(i).getPreviewImage() == null&&answerResults.get(i).getOriginalImage() == null){
                                    if (answerResults.get(i).getAnswers() == null||answerResults.get(i).getAnswers().size()== 0){
                                        Log.w("YKT",answerResults.get(i).getWisdomClassId()+"条数据没有预览图");
                                    }else{
                                        studentHandWriteList.add(bean);
                                    }
                                }else{
                                    studentHandWriteList.add(bean);
                                }
                            }
                        }
                        for (int i = 0; i < jsonWithStudents.size(); i++) {
                            if (jsonWithStudents.get(i).getBlackboardWrite() != null&&jsonWithStudents.get(i).getBlackboardWrite().size()>0){
                                //封装板书记录的集合
                                for (int i1 = 0; i1 < jsonWithStudents.get(i).getBlackboardWrite().size(); i1++) {
                                    BlackboardWrite bean = new BlackboardWrite();
                                    String str = jsonWithStudents.get(i).getName();
                                    String[] strs = str.split(" ");
                                    StringBuffer sb = new StringBuffer();
                                    for (int i2 = 0; i2 < strs.length; i2++) {
                                        if ("临时课程".equals(strs[i2])){
                                            sb.append(strs[i2]+" ");
                                            break;
                                        }else{
                                            if (i2 != 0){
                                                sb.append(strs[i2]+" ");
                                            }
                                        }
                                    }
                                    bean.setOriginalImageAttachmentId(jsonWithStudents.get(i).getBlackboardWrite().get(i1).getOriginalImageAttachmentId());
                                    bean.setPreviewImageAttachmentId(jsonWithStudents.get(i).getBlackboardWrite().get(i1).getPreviewImageAttachmentId());
                                    bean.setTeacherName(jsonWithStudents.get(i).getTeacehrName());
                                    bean.setName(sb.toString()+name+"blackBorad"+(i1+1)+".jpg");
                                    bean.setDateTime(jsonWithStudents.get(i).getDateTime());
                                    bean.setPreviewImage(jsonWithStudents.get(i).getBlackboardWrite().get(i1).getPreviewImage());
                                    bean.setOriginalImage(jsonWithStudents.get(i).getBlackboardWrite().get(i1).getOriginalImage());
                                    blackBoradList.add(bean);
                                }

                                //封装教学课件
                                for (int i1 = 0; i1 < jsonWithStudents.get(i).getCourseware().size(); i1++) {
                                    CourseWare c = new CourseWare();
                                    String str = jsonWithStudents.get(i).getName();
                                    String[] strs = str.split(" ");
                                    StringBuffer sb = new StringBuffer();
                                    for (int i2 = 0; i2 < strs.length; i2++) {
                                        if ("临时课程".equals(strs[i2])){
                                            sb.append(strs[i2]+" ");
                                            break;
                                        }else{
                                            if (i2 != 0){
                                                sb.append(strs[i2]+" ");
                                            }
                                        }
                                    }
                                    c.setTeacherName(jsonWithStudents.get(i).getTeacehrName());
                                    c.setTitle(sb.toString()+name);
                                    c.setSex(jsonWithStudents.get(i).getSex());
                                    c.setDateTime(jsonWithStudents.get(i).getDateTime());
                                    c.setFileName(jsonWithStudents.get(i).getCourseware().get(i1).getName());
                                    c.setUrl(jsonWithStudents.get(i).getCourseware().get(i1).getUrl());
                                    teachResouseList.add(c);
                                }
                            }
                        }
                    }

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadYKTDatas(searchPosition);
                            switch (searchPosition){
                                case 0:
                                    setLoading1(false);
                                    break;
                                case 1:
                                    setLoading2(false);
                                    break;
                                case 2:
                                    setLoading3(false);
                                    break;
                            }
                            showDialog(false,null);
                        }
                    },5);
                } catch (IOException e) {

                    switch (searchPosition){
                        case 0:
                            setLoading1(false);
                            break;
                        case 1:
                            setLoading2(false);
                            break;
                        case 2:
                            setLoading3(false);
                            break;
                    }
                    showDialog(false,null);
                    ToastUtil.showMessage("获取失败，请检查网络..");
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 教师根据条件获取课程数据
     */
    public void getHandWriteForTeacher(final int searchPosition){
        //判断当前是否在加载数据
        switch (searchPosition){
            case 0:
                if (isLoading1){
                    showDialog(false,null);
                    return;
                }else{
                    setLoading1(true);
                }
                break;
            case 1:
                if (isLoading1){
                    showDialog(false,null);
                    return;
                }else{
                    setLoading2(true);
                }
                break;
            case 2:
                if (isLoading2){
                    showDialog(false,null);
                    return;
                }else{
                    setLoading3(true);
                }
                break;
        }

        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    //根据有数据的条件获取手写笔记
                    HashMap<String,ParameterValue> hashMap = new HashMap<String, ParameterValue>();
                    hashMap.putAll(ECApplication.getInstance().getLoginUrlMap());
                    hashMap.put("teacherId",new ParameterValue(ECApplication.getInstance().getUserForYKT().getId()));
                    hashMap.put("eclassId",new ParameterValue(eclassId));
                    hashMap.put("courseId",new ParameterValue(courseId));
                    hashMap.put("startDate",new ParameterValue(startDate));
                    hashMap.put("endDate",new ParameterValue(endDate));
                    String handWriteJson = ZddcUtil.getWisdomClassesJsonWithTeacher(hashMap);
                    ArrayList<WisdomClassesJsonWithTeacher> jsonWithTeachers = new Gson().fromJson(handWriteJson, new TypeToken<List<WisdomClassesJsonWithTeacher>>(){}.getType());

                    //获取显示名称（学科）
                    String name = "";
                    for (int i = 0; i < courses.size(); i++) {
                        for (int i1 = 0; i1 < courses.get(i).getCourses().size(); i1++) {
                            if (courseId.equals(courses.get(i).getCourses().get(i1).getId())){
                                name = courses.get(i).getCourses().get(i1).getName();
                                break;
                            }
                        }
                        break;
                    }
                    handWriteBeans.clear();
                    blackBoradList.clear();
                    teachResouseList.clear();
                    if (jsonWithTeachers != null && jsonWithTeachers.size()> 0){
                        int count = 0;
                        for (int i = 0; i < jsonWithTeachers.size(); i++) {
                            //封装即将展示手写笔记的集合
                            for (int i1 = 0; i1 < jsonWithTeachers.get(i).getQuestions2().size(); i1++) {
                                count++;
                                HandWriteBean bean = new HandWriteBean();
                                bean.setQuestionId(jsonWithTeachers.get(i).getQuestions2().get(i1).getId());
                                String questionName = jsonWithTeachers.get(i).getName()+" "+name+" question"+count;
                                String time = jsonWithTeachers.get(i).getQuestions2().get(i1).getStartTime();
                                String[] strs = questionName.split(" ");
                                StringBuffer sb = new StringBuffer();
                                for (int i2 = 0; i2 < strs.length; i2++) {
                                    if (i2 != 0){
                                        sb.append(strs[i2]+"  ");
                                    }else{
                                        time = strs[0]+"   "+time;
                                    }
                                }
                                bean.setOriginalImageAttachmentQuestionId(jsonWithTeachers.get(i).getQuestions2().get(i1).getOriginalImageAttachmentId());
                                bean.setPreviewImageAttachmentQuestionId(jsonWithTeachers.get(i).getQuestions2().get(i1).getPreviewImageAttachmentId());
                                bean.setSex(jsonWithTeachers.get(i).getSex());
                                bean.setQuestionName(sb.toString());
                                bean.setDateTime(time);
                                bean.setPreviewQuestionImg(jsonWithTeachers.get(i).getQuestions2().get(i1).getPreviewImage());
                                bean.setOriginaQuestionlImg(jsonWithTeachers.get(i).getQuestions2().get(i1).getOriginalImage());
                                handWriteBeans.add(bean);
                            }
                            //封装板书记录的集合
                            for (int i1 = 0; i1 < jsonWithTeachers.get(i).getBlackboardWrite().size(); i1++) {
                                BlackboardWrite bean = new BlackboardWrite();
                                String str = jsonWithTeachers.get(i).getName();
                                String[] strs = str.split(" ");
                                StringBuffer sb = new StringBuffer();
                                for (int i2 = 0; i2 < strs.length; i2++) {
                                    if ("临时课程".equals(strs[i2])){
                                        sb.append(strs[i2]+" ");
                                        break;
                                    }else{
                                        if (i2 != 0){
                                            sb.append(strs[i2]+" ");
                                        }
                                    }
                                }
                                bean.setOriginalImageAttachmentId(jsonWithTeachers.get(i).getBlackboardWrite().get(i1).getOriginalImageAttachmentId());
                                bean.setPreviewImageAttachmentId(jsonWithTeachers.get(i).getBlackboardWrite().get(i1).getPreviewImageAttachmentId());
                                bean.setName(sb.toString()+name+"blackBorad"+(i1+1)+".jpg");
                                bean.setDateTime(jsonWithTeachers.get(i).getDateTime());
                                bean.setTeacherName(ECApplication.getInstance().getUserForYKT().getName());
                                bean.setPreviewImage(jsonWithTeachers.get(i).getBlackboardWrite().get(i1).getPreviewImage());
                                bean.setOriginalImage(jsonWithTeachers.get(i).getBlackboardWrite().get(i1).getOriginalImage());
                                blackBoradList.add(bean);
                            }
                            //封装教学课件
                            for (int i1 = 0; i1 < jsonWithTeachers.get(i).getCourseware().size(); i1++) {
                                CourseWare c = new CourseWare();
                                String str = jsonWithTeachers.get(i).getName();
                                String[] strs = str.split(" ");
                                StringBuffer sb = new StringBuffer();

                                for (int i2 = 0; i2 < strs.length; i2++) {
                                    if ("临时课程".equals(strs[i2])){
                                        sb.append(strs[i2]+" ");
                                        break;
                                    }else{
                                        if (i2 != 0){
                                            sb.append(strs[i2]+" ");
                                        }
                                    }
                                }
                                c.setAttachmentId(jsonWithTeachers.get(i).getCourseware().get(i1).getAttachmentId());
                                c.setSex(jsonWithTeachers.get(i).getSex());
                                c.setTitle(sb.toString()+name);
                                c.setDateTime(jsonWithTeachers.get(i).getDateTime());
                                c.setFileName(jsonWithTeachers.get(i).getCourseware().get(i1).getName());
                                c.setUrl(jsonWithTeachers.get(i).getCourseware().get(i1).getUrl());
                                teachResouseList.add(c);
                            }
                        }
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadYKTDatas(searchPosition);
                            switch (searchPosition){
                                case 0:
                                    setLoading1(false);
                                    break;
                                case 1:
                                    setLoading2(false);
                                    break;
                                case 2:
                                    setLoading3(false);
                                    break;
                            }
                            showDialog(false,null);
                        }
                    },5);
                } catch (IOException e) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchPosition){
                                case 0:
                                    setLoading1(false);
                                    break;
                                case 1:
                                    setLoading2(false);
                                    break;
                                case 2:
                                    setLoading3(false);
                                    break;
                            }
                            ToastUtil.showMessage("获取失败..请查看网络!");
                            showDialog(false,null);
                        }
                    },5);
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //全选确定
    public void onMuchSelectOK(){
        if (isMuchSelectMap.size()>0){
            ArrayList<BlackboardWrite> list = new ArrayList<>();
            for (BlackboardWrite v : isMuchSelectMap.values()) {
                list.add(v);
            }
            MainActivity.map.put("BlackBorad",list);
            isMuchSelectMap.clear();
            BlackBoradAdapter.isMuchSelect = false;
            MainActivity.showSelectBatchLinear(false);
            setAllSelectText(false);
            blackBoradAdapter.notifyDataSetChanged();
            startActivity(new Intent(context,UpFileActivity.class));
        }else{
            ToastUtil.showMessage("请选择板书记录..");
        }
    }
    //取消刷新
    public void notifyForMuchSelect(){
        if (blackBoradAdapter != null){
            blackBoradAdapter.notifyDataSetChanged();
        }
    }
    //全选
    public void selectAll(boolean isAllSelect){
        if (isAllSelect){
            if (blackBoradList != null &&blackBoradList.size()>0){
                isMuchSelectMap.clear();
                for (int i = 0; i < blackBoradList.size(); i++) {
                    isMuchSelectMap.put(i,blackBoradList.get(i));
                }
            }
        }else{
            isMuchSelectMap.clear();
        }
        blackBoradAdapter.notifyDataSetChanged();
    }
    private ArrayList<HandWriteBean> handWriteBeans = new ArrayList<>();
    private Handler handler = new Handler();
    public static int YKTPOSITION;
    private void loadYKTDatas(int position) {
        switch (position){
            case 0:
                YKTPOSITION = 0;
                if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
                    if (handWriteAdapter == null){
                        handWriteAdapter = new HandWriteAdapter(handWriteBeans,context);
                        handWriteLV.setAdapter(handWriteAdapter);
                        handWriteLV.setEmptyView(handWrite.findViewById(R.id.handwriteempty));
                    }else{
                        handWriteAdapter.notifyDataSetChanged();
                    }
                }else if (ECApplication.getInstance().getUserForYKT().getType().equals("0")){
                    if (studentHandWriteAdapter == null){
                        studentHandWriteAdapter = new StudentHandWriteAdapter(studentHandWriteList,context);
                        handWriteLV.setAdapter(studentHandWriteAdapter);
                        handWriteLV.setEmptyView(handWrite.findViewById(R.id.handwriteempty));
                    }else{
                        studentHandWriteAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case 1:
                YKTPOSITION = 1;
                if (blackBoradAdapter == null){
                    blackBoradAdapter = new BlackBoradAdapter(blackBoradList,context);
                    blackBoradLV.setAdapter(blackBoradAdapter);
                    blackBoradLV.setEmptyView(balckBorad.findViewById(R.id.balckBoradEmpty));
                }else{
                    blackBoradAdapter.notifyDataSetChanged();
                }

                break;
            case 2:
                YKTPOSITION = 2;
                if (teachCourseAdapter == null){
                    teachCourseAdapter = new TeachCourseAdapter(teachResouseList,context,YKTFragment.this);
                    teachResouseLV.setAdapter(teachCourseAdapter);
                    teachResouseLV.setEmptyView(teachResouse.findViewById(R.id.teachResouseEmpty));
                }else{
                    teachCourseAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ykt, container, false);
    }

    private boolean isLoading1 = false;
    private boolean isLoading2 = false;
    private boolean isLoading3 = false;

    public boolean isLoading1() {
        return isLoading1;
    }

    public void setLoading1(boolean loading1) {
        isLoading1 = loading1;
    }

    public boolean isLoading2() {
        return isLoading2;
    }

    public void setLoading2(boolean loading2) {
        isLoading2 = loading2;
    }

    public boolean isLoading3() {
        return isLoading3;
    }

    public void setLoading3(boolean loading3) {
        isLoading3 = loading3;
    }
}
