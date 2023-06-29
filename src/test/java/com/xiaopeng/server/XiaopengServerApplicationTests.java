package com.xiaopeng.server;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiaopeng.server.app.bean.common.SimpleDate;
import com.xiaopeng.server.app.bean.pojo.User;
import com.xiaopeng.server.app.bean.utils.CloneUtils;
import com.xiaopeng.server.vx.NewsTask;
import com.xiaopeng.server.vx.TimedTask;
import com.xiaopeng.server.vx.config.AutoLog;
import com.xiaopeng.server.vx.utils.RSAUtil;
import org.jsoup.internal.StringUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
class XiaopengServerApplicationTests {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
//    @Autowired
//    private RedisUtil redisUtil;

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
    @Test
    public void test(){
        stringRedisTemplate.opsForValue().set("1","11111");
        System.out.println(stringRedisTemplate.opsForValue().get("1"));
//        redisUtil.set("2","2222");
//        System.out.println(redisUtil.get("2"));
    }

    @Test
    public void test990(){
        //导入
        List<Long> longs = new ArrayList<>();
        //数据库
        List<Long> longs2 = new ArrayList<>();
        longs.add(1L);
        longs.add(2L);
        longs.add(3L);
        longs.add(4L);
        longs.add(5L);
        longs2.add(1L);
        longs2.add(2L);
        List<Long> diff = longs.stream()
                .filter(l -> !longs2.contains(l))
                .collect(Collectors.toList());

        System.out.println(JSONObject.toJSONString(diff));
    }
    @Test
    public void test9900(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        String format = DateUtil.format(DateUtil.beginOfMonth(c.getTime()), "yyyy-MM-dd");
        String eformat = DateUtil.format(DateUtil.endOfMonth(c.getTime()), "yyyy-MM-dd");
        System.out.println(format);
        System.out.println(eformat);
    }
    /**
     * 热搜
     */
    @Autowired
    private NewsTask newsTask;
    @Test
    public void testNew(){
        newsTask.grabBaiduHotNewsJson();
    }


    @Test
    public void testRSA(){

            //解密数据
            try {
                //生成公钥和私钥
                Map<Integer, String> keyMap = (Map<Integer, String>)RSAUtil.genKeyPair();
                String publicKey = keyMap.get(0);
                System.out.println("公钥:" + publicKey);
                String privateKey = keyMap.get(1);
                System.out.println("私钥:" + privateKey);

                Map params = new HashMap();
                params.put("cid", "kmdl11061");
                params.put("cdate", "1571281896");
                //参数进行字典排序, 待签名字符串
                String sortStr = RSAUtil.getFormatParams(params);
                // 使用md5算法加密待加密字符串并转为大写即为sign
                String sign = SecureUtil.md5(sortStr).toUpperCase();

                String orgData = "123456781668745014120123D";
                System.out.println("原数据：" + orgData);
                String encryptStr =RSAUtil.encrypt(orgData,publicKey);
                System.out.println("加密结果：" + encryptStr);

                String decryptStr = RSAUtil.decrypt(encryptStr,privateKey);
                System.out.println("解密结果：" + decryptStr);

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    @Test
    public void demo(){
        String s="228,229,230,231,342,343,258,898,899,900,901,902,903,904,906,907,908,909,910,911,1178,1205,1265,1272,1334,1335,1336,1376,11306,11307,11319,15,912,913,914,915,916,917,928,929,930,931,932,11311,11312,11313,11314,11315,11316,11317,933,934,935,11309,936,937,938,939,940,941,942,17,18,19,20,105,927,11284,80,83,106,319,321,571,572,573,574,575,576,577,578,579,580,1253,24,225,357,358,359,360,361,362,364,366,918,919,920,921,922,923,1206,1282,1413,1414,11283,833,834,835,1257,1258,244,960,961,962,1259,335,368,617,618,619,620,621,622,623,624,625,626,627,1302,22,276,277,420,527,528,529,530,1247,1262,466,467,468,469,470,946,947,948,472,473,474,475,476,1189,478,479,480,481,482,483,484,485,836,1242,1243,1244,1245,487,488,489,490,491,492,493,494,372,496,497,498,499,500,501,502,504,505,506,507,508,510,511,512,513,514,516,517,23,365,520,521,522,523,524,525,526,1246,30,82,320,407,567,568,569,570,678,832,1214,1239,41,453,586,587,589,590,591,592,593,594,595,431,596,597,1212,599,600,601,603,604,605,606,608,609,610,611,612,613,614,615,616,823,824,837,943,944,945,1078,1181,314,801,802,803,804,805,806,807,808,809,810,811,950,951,952,953,954,955,956,957,958,959,1013,1014,1015,53,54,55,70,688,689,690,691,1240,1286,1477,1478,1492,11294,11295,66,189,659,660,661,662,663,681,682,683,684,685,65,692,693,694,68,695,696,697,698,699,700,701,1254,1261,1274,1275,721,722,723,724,725,726,727,728,763,1278,1281,1291,1294,1295,1338,1489,764,765,766,767,793,1280,1359,1360,1490,11277,794,795,796,797,814,1293,1298,1299,1300,1301,11279,11280,11281,36,43,69,664,665,666,35,37,38,39,40,347,667,668,669,670,671,672,673,44,45,1175,1266,674,675,676,677,679,812,58,59,310,1109,1110,312,313,440,1111,1112,1113,1114,1115,1176,73,1116,1117,1119,1120,1121,1123,1124,1125,1126,1127,74,75,1129,1130,1131,1248,1132,1133,1134,1135,1136,1137,1138,1139,1216,1217,1218,1219,532,533,534,535,537,538,539,540,541,543,544,545,546,547,549,551,552,553,555,556,557,558,559,560,562,563,564,565,1220,1016,1017,1018,1019,1020,1021,1022,1023,1024,1025,1026,1027,1028,1029,1030,1039,1040,1041,1042,1043,1044,1045,1046,1047,1048,1049,1050,1051,1052,1053,1054,1055,1056,1057,1058,1059,1060,1061,1062,1063,1064,1065,1270,11289,11291,1066,1067,1068,1069,1070,1071,1072,1073,1080,1081,1082,1207,1241,1074,1075,1076,1077,84,110,111,128,129,130,131,132,133,134,135,136,137,266,1273,140,142,143,273,1271,261,262,263,279,280,282,1285,109,427,1162,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1251,222,353,354,355,356,648,649,650,651,652,1211,1255,108,430,457,459,828,829,830,831,1260,644,645,646,647,1213,637,638,639,640,641,642,1362,1363,11282,643,825,826,827,422,820,821,822,11293,817,818,819,1276,1277,1284,1292,1342,730,731,732,733,1252,448,741,742,743,1288,1289,1290,1296,1348,749,750,751,752,753,787,788,789,790,791,798,799,800,847,848,849,850,851,852,853,859,860,861,862,863,864,854,855,856,857,858,11296,865,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,965,966,967,968,969,970,88,275,971,972,973,1079,1007,1008,1009,1010,1011,1012,223,334,1152,1153,1154,1155,1156,1157,1158,1160,1161,1177,99,1145,1146,1147,1148,1174,246,838,1150,1151,247,978,979,980,981,1264,982,983,984,985,986,987,988,989,990,991,992,993,994,995,1003,1004,1005,1006,413,414,415,416,423,424,1085,1086,1087,1088,1089,1090,1249,1250,1267,1268,1091,1092,1093,1094,786,813,1084,1097,1210,89,90,180,182,245,183,184,193,268,272,371,406,1223,1224,1225,1226,1227,1228,1229,1230,1231,1232,1233,1234,1235,1236,1237,1238,1468,1484,1303,1304,1350,1351,1352,1353,1354,1355,1356,1357,1358,1366,1367,1368,1369,1370,1371,1372,1373,1374,1377,1378,1379,1380,1381,1382,1384,1386,1387,1388,1391,1392,1393,1394,1395,1396,1397,1398,1399,1400,1401,1402,1403,1404,1405,1406,1407,1408,1409,1410,1411,1412,1415,1416,1417,1418,1425,1426,1427,1429,1430,1431,1432,1433,1434,1435,1436,1437,1438,1439,1440,1441,1442,1443,1444,1445,1446,1447,1448,1449,1450,1451,1452,1453,1454,1455,1456,1457,1458,1459,1460,1461,1462,1463,1464,1469,1470,1471,1472,1473,1474,1475,1476,1479,1480,1481,1482,11247,11275,11286,11287,11288,11292,11301,11324,11325,11326,11327,11328,11320,11321,11322,11323,11339,11340,11341,11329,11330,11331,11332,11333,11334,11335,11336,11310,11318,11338,11337,11342,12335,12336";
        String[] split = s.split(",");
        List<String> categoryIdList = new ArrayList<String>(split.length);
        Collections.addAll(categoryIdList, split);
        List<Integer> proIds = categoryIdList.stream().map(e -> { return Integer.valueOf(e); }).collect(Collectors.toList());
        System.out.println(JSONObject.toJSONString(proIds));
    }
    @Test
    public void xiaoshuo(){
        String s="[{\"date\":\"2023-06-12\",\"name\":\"11\"},{\"date\":\"2023-03-12\",\"name\":\"22\"},{\"date\":\"2023-04-12\",\"name\":\"33\"}]";
        JSONArray jsonArray = JSONObject.parseArray(s);
        List<Object> collect = jsonArray.stream().filter(e -> ObjectUtil.isNotEmpty((JSONObject.parseObject(JSONObject.toJSONString(e))).getDate("date"))).sorted(Comparator.comparing(obj -> ((JSONObject) obj).getDate("date"))).collect(Collectors.toList());
        System.out.println(JSONObject.toJSONString(collect));
    }
    @Test
    public void DateWeek(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal=Calendar.getInstance();
        // 设置每周的开始日期
        cal.setFirstDayOfWeek(Calendar.SATURDAY);
        cal.setMinimalDaysInFirstWeek(3);
        cal.set(Calendar.YEAR, 2023);
        cal.set(Calendar.WEEK_OF_YEAR, 20);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        //+4为周五的日期
        cal.add(Calendar.DAY_OF_WEEK, 6);
        System.out.println(sdf.format(cal.getTime()));
    }
    @Test
    public void test0628(){
//        BigDecimal num = new BigDecimal(0);
//        NumberFormat nf = NumberFormat.getPercentInstance();
//        nf.setMinimumFractionDigits(2);
//        String percentStr = nf.format(num);
//        System.out.println(percentStr);
        BigDecimal bigDecimal = new BigDecimal(-0.21);
        BigDecimal bigDecimal2 = new BigDecimal(0.21);
        BigDecimal subtract = bigDecimal.subtract(bigDecimal2);
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        String percentStr = nf.format(subtract);
        System.out.println(percentStr);
    }
    public String getPercentage(BigDecimal subtract){
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        return  nf.format(subtract);
    }
}
