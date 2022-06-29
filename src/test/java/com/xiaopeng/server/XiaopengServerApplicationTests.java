package com.xiaopeng.server;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiaopeng.server.app.bean.common.SimpleDate;
import com.xiaopeng.server.app.bean.utils.CloneUtils;
import com.xiaopeng.server.app.bean.pojo.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class XiaopengServerApplicationTests {

    @Test
    void contextLoads() {
        //根据对象中某个值拆分成多个对象
        System.out.println(new Date().getTime());
        List<User> users = new ArrayList<>();
        for (int i = 1; i < 1000; i++) {
            User user = new User();
            user.setId(i);
            user.setName("1,2,3,4,5,6,7,8,9");
            users.add(user);
        }
        List<User> extendNotices = new ArrayList<>();
        for (User o : users) {
            if (StringUtils.isNotBlank(o.getName())) {
                String[] split = o.getName().split(",");
                if (split.length > 1) {
                    for (int i = 1; i < split.length; i++) {
                        User clone = CloneUtils.clone(o);
                        clone.setName(split[i]);
                        extendNotices.add(clone);
                    }
                    o.setName(split[0]);
                }
            }
        }
        users.addAll(extendNotices);
        System.out.println(new Date().getTime());
    }
    @Test
    public void testxiao(){
        //根据对象中某个值拆分成多个对象
        System.out.println(new Date().getTime());
        List<User> users = new ArrayList<>();
        List<User> users2 = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setId(i);
            user.setName("1,2,3,4,5,6,7,8,9");
            users.add(user);
        }
        for (User user1 : users) {
            String[] split = user1.getName().split(",");
            for (String s : split) {
                User user2 = new User();
                user2.setId(user1.getId());
                user2.setName(s);
                users2.add(user2);
            }
        }
        System.out.println(new Date().getTime());
    }

    @Test
    public void test20220610(){
//        new Thread(){
//            @Override
//            public void run() {
//                log.info(SimpleDate.getDefaultDate(new Date()));
//            }
//        }.start();
//        new Thread(){
//            @Override
//            public void run() {
//                log.info(SimpleDate.getDefaultDate(new Date()));
//            }
//        }.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(SimpleDate.getDefaultDate(new Date()));
            }
        }).start();
        System.out.println(1111);
    }
    /**
     *
     * 按照指定对象的字段排序(正序)
     *
     * @param list
     * @param param 排序字段
     * @param <T>
     * @throws IntrospectionException
     */
    public static final <T> void ObjSort(List<T> list , String param) throws IntrospectionException {
        Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                Class<?> type = o1.getClass();
                PropertyDescriptor descriptor1 = null;
                try {
                    descriptor1 = new PropertyDescriptor( param, type );
                    Method readMethod1 = descriptor1.getReadMethod();

                    PropertyDescriptor descriptor2 = new PropertyDescriptor( param, type );
                    Method readMethod2 = descriptor2.getReadMethod();
                    return readMethod1.invoke(o1).toString().compareTo(readMethod2.invoke(o2).toString());
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
    }
    @Test
    public void getSort(){
        ArrayList<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1);
        user.setName("xiaopeng");
        User user1 = new User();
        user1.setId(2);
        user1.setName("小明");
        User user2= new User();
        user2.setId(3);
        user2.setName("小刘");
        users.add(user1);
        users.add(user);
        users.add(user2);
        try {
            ObjSort(users, "id");
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(JSONObject.toJSONString(users));
    }
    private volatile List<Integer> list = new ArrayList<>();
    @Test
    public void volatileTest(){

            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        list.add(1);
                    }
                }
            }.start();
            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        list.add(2);
                    }
                }
            }.start();
            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        list.add(3);
                    }
                }
            }.start();
        System.out.println(JSONObject.toJSONString(list));
    }
}
