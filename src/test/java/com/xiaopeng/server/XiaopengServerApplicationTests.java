package com.xiaopeng.server;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.impl.BasicCaptchaTrackValidator;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiaopeng.server.app.bean.common.SimpleDate;
import com.xiaopeng.server.app.bean.pojo.User;
import com.xiaopeng.server.app.bean.utils.CloneUtils;
import com.xiaopeng.server.vx.NewsTask;
import com.xiaopeng.server.vx.entity.WeatherEntity;
import com.xiaopeng.server.vx.service.WeatherService;
import groovy.util.logging.Slf4j;
import org.jsoup.internal.StringUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.crypto.Data;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
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
    public void testxiao() {
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
        System.out.println(JSONObject.toJSONString(users2));
        System.out.println(new Date().getTime());
    }

    @Test
    public void test20220610() {
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
//        new Thread(() -> System.out.println(SimpleDate.getDefaultDate(new Date()))).start();
//        System.out.println(1111);
//
        String s ="噶几嘎婆";
        String encode = URLEncodeUtil.encode(s);
        System.out.println(encode);
    }

    /**
     * 按照指定对象的字段排序(正序)
     *
     * @param list
     * @param param 排序字段
     * @param <T>
     */
    public static final <T> void ObjSort(List<T> list, String param) {
        Collections.sort(list, (o1, o2) -> {
            Class<?> type = o1.getClass();
            PropertyDescriptor descriptor1;
            try {
                descriptor1 = new PropertyDescriptor(param, type);
                Method readMethod1 = descriptor1.getReadMethod();

                PropertyDescriptor descriptor2 = new PropertyDescriptor(param, type);
                Method readMethod2 = descriptor2.getReadMethod();
                return readMethod1.invoke(o1).toString().compareTo(readMethod2.invoke(o2).toString());
            } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return -1;
        });
    }

    @Test
    public void getSort() {
        ArrayList<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1);
        user.setName("xiaopeng");
        User user1 = new User();
        user1.setId(2);
        user1.setName("小明");
        User user2 = new User();
        user2.setId(3);
        user2.setName("小刘");
        users.add(user1);
        users.add(user);
        users.add(user2);
        try {
            ObjSort(users, "id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(JSONObject.toJSONString(users));
    }

    private final List<Integer> list = new ArrayList<>();

    @Test
    public void volatileTest() {

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                list.add(1);
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                list.add(2);
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                list.add(3);
            }
        }).start();
        System.out.println(JSONObject.toJSONString(list));
    }

    @Test
    public void test() {
        stringRedisTemplate.opsForValue().set("1", "11111");
        System.out.println(stringRedisTemplate.opsForValue().get("1"));
//        redisUtil.set("2","2222");
//        System.out.println(redisUtil.get("2"));
    }

    @Test
    public void test990() {
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
    public void test9900() {
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
    public void testNew() {
        newsTask.grabBaiduHotNewsJson();
    }


//    @Test
//    public void testRSA() {
//
//        //解密数据
//        try {
//            //生成公钥和私钥
//            Map<Integer, String> keyMap = (Map<Integer, String>) RSAUtil.genKeyPair();
//            String publicKey = keyMap.get(0);
//            System.out.println("公钥:" + publicKey);
//            String privateKey = keyMap.get(1);
//            System.out.println("私钥:" + privateKey);
//
//            Map params = new HashMap();
//            params.put("cid", "kmdl11061");
//            params.put("cdate", "1571281896");
//            //参数进行字典排序, 待签名字符串
//            String sortStr = RSAUtil.getFormatParams(params);
//            // 使用md5算法加密待加密字符串并转为大写即为sign
//            String sign = SecureUtil.md5(sortStr).toUpperCase();
//
//            String orgData = "123456781668745014120123D";
//            System.out.println("原数据：" + orgData);
//            String encryptStr = RSAUtil.encrypt(orgData, publicKey);
//            System.out.println("加密结果：" + encryptStr);
//
//            String decryptStr = RSAUtil.decrypt(encryptStr, privateKey);
//            System.out.println("解密结果：" + decryptStr);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    @Test
    public void demo() {
        String s = "228,229,230,231,342,343,258,898,899,900,901,902,903,904,906,907,908,909,910,911,1178,1205,1265,1272,1334,1335,1336,1376,11306,11307,11319,15,912,913,914,915,916,917,928,929,930,931,932,11311,11312,11313,11314,11315,11316,11317,933,934,935,11309,936,937,938,939,940,941,942,17,18,19,20,105,927,11284,80,83,106,319,321,571,572,573,574,575,576,577,578,579,580,1253,24,225,357,358,359,360,361,362,364,366,918,919,920,921,922,923,1206,1282,1413,1414,11283,833,834,835,1257,1258,244,960,961,962,1259,335,368,617,618,619,620,621,622,623,624,625,626,627,1302,22,276,277,420,527,528,529,530,1247,1262,466,467,468,469,470,946,947,948,472,473,474,475,476,1189,478,479,480,481,482,483,484,485,836,1242,1243,1244,1245,487,488,489,490,491,492,493,494,372,496,497,498,499,500,501,502,504,505,506,507,508,510,511,512,513,514,516,517,23,365,520,521,522,523,524,525,526,1246,30,82,320,407,567,568,569,570,678,832,1214,1239,41,453,586,587,589,590,591,592,593,594,595,431,596,597,1212,599,600,601,603,604,605,606,608,609,610,611,612,613,614,615,616,823,824,837,943,944,945,1078,1181,314,801,802,803,804,805,806,807,808,809,810,811,950,951,952,953,954,955,956,957,958,959,1013,1014,1015,53,54,55,70,688,689,690,691,1240,1286,1477,1478,1492,11294,11295,66,189,659,660,661,662,663,681,682,683,684,685,65,692,693,694,68,695,696,697,698,699,700,701,1254,1261,1274,1275,721,722,723,724,725,726,727,728,763,1278,1281,1291,1294,1295,1338,1489,764,765,766,767,793,1280,1359,1360,1490,11277,794,795,796,797,814,1293,1298,1299,1300,1301,11279,11280,11281,36,43,69,664,665,666,35,37,38,39,40,347,667,668,669,670,671,672,673,44,45,1175,1266,674,675,676,677,679,812,58,59,310,1109,1110,312,313,440,1111,1112,1113,1114,1115,1176,73,1116,1117,1119,1120,1121,1123,1124,1125,1126,1127,74,75,1129,1130,1131,1248,1132,1133,1134,1135,1136,1137,1138,1139,1216,1217,1218,1219,532,533,534,535,537,538,539,540,541,543,544,545,546,547,549,551,552,553,555,556,557,558,559,560,562,563,564,565,1220,1016,1017,1018,1019,1020,1021,1022,1023,1024,1025,1026,1027,1028,1029,1030,1039,1040,1041,1042,1043,1044,1045,1046,1047,1048,1049,1050,1051,1052,1053,1054,1055,1056,1057,1058,1059,1060,1061,1062,1063,1064,1065,1270,11289,11291,1066,1067,1068,1069,1070,1071,1072,1073,1080,1081,1082,1207,1241,1074,1075,1076,1077,84,110,111,128,129,130,131,132,133,134,135,136,137,266,1273,140,142,143,273,1271,261,262,263,279,280,282,1285,109,427,1162,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1251,222,353,354,355,356,648,649,650,651,652,1211,1255,108,430,457,459,828,829,830,831,1260,644,645,646,647,1213,637,638,639,640,641,642,1362,1363,11282,643,825,826,827,422,820,821,822,11293,817,818,819,1276,1277,1284,1292,1342,730,731,732,733,1252,448,741,742,743,1288,1289,1290,1296,1348,749,750,751,752,753,787,788,789,790,791,798,799,800,847,848,849,850,851,852,853,859,860,861,862,863,864,854,855,856,857,858,11296,865,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,965,966,967,968,969,970,88,275,971,972,973,1079,1007,1008,1009,1010,1011,1012,223,334,1152,1153,1154,1155,1156,1157,1158,1160,1161,1177,99,1145,1146,1147,1148,1174,246,838,1150,1151,247,978,979,980,981,1264,982,983,984,985,986,987,988,989,990,991,992,993,994,995,1003,1004,1005,1006,413,414,415,416,423,424,1085,1086,1087,1088,1089,1090,1249,1250,1267,1268,1091,1092,1093,1094,786,813,1084,1097,1210,89,90,180,182,245,183,184,193,268,272,371,406,1223,1224,1225,1226,1227,1228,1229,1230,1231,1232,1233,1234,1235,1236,1237,1238,1468,1484,1303,1304,1350,1351,1352,1353,1354,1355,1356,1357,1358,1366,1367,1368,1369,1370,1371,1372,1373,1374,1377,1378,1379,1380,1381,1382,1384,1386,1387,1388,1391,1392,1393,1394,1395,1396,1397,1398,1399,1400,1401,1402,1403,1404,1405,1406,1407,1408,1409,1410,1411,1412,1415,1416,1417,1418,1425,1426,1427,1429,1430,1431,1432,1433,1434,1435,1436,1437,1438,1439,1440,1441,1442,1443,1444,1445,1446,1447,1448,1449,1450,1451,1452,1453,1454,1455,1456,1457,1458,1459,1460,1461,1462,1463,1464,1469,1470,1471,1472,1473,1474,1475,1476,1479,1480,1481,1482,11247,11275,11286,11287,11288,11292,11301,11324,11325,11326,11327,11328,11320,11321,11322,11323,11339,11340,11341,11329,11330,11331,11332,11333,11334,11335,11336,11310,11318,11338,11337,11342,12335,12336";
        String[] split = s.split(",");
        List<String> categoryIdList = new ArrayList<String>(split.length);
        Collections.addAll(categoryIdList, split);
        List<Integer> proIds = categoryIdList.stream().map(Integer::valueOf).collect(Collectors.toList());
        System.out.println(JSONObject.toJSONString(proIds));
    }

    @Test
    public void xiaoshuo() {
        String s = "[{\"date\":\"2023-06-12\",\"name\":\"11\"},{\"date\":\"2023-03-12\",\"name\":\"22\"},{\"date\":\"2023-04-12\",\"name\":\"33\"}]";
        JSONArray jsonArray = JSONObject.parseArray(s);
        List<Object> collect = jsonArray.stream().filter(e -> ObjectUtil.isNotEmpty((JSONObject.parseObject(JSONObject.toJSONString(e))).getDate("date"))).sorted(Comparator.comparing(obj -> ((JSONObject) obj).getDate("date"))).collect(Collectors.toList());
        System.out.println(JSONObject.toJSONString(collect));
    }

    @Test
    public void DateWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
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
    public void test0628() {
//        BigDecimal num = new BigDecimal(0);
//        NumberFormat nf = NumberFormat.getPercentInstance();
//        nf.setMinimumFractionDigits(2);
//        String percentStr = nf.format(num);
//        System.out.println(percentStr);
        BigDecimal bigDecimal = BigDecimal.valueOf(-0.21);
        BigDecimal bigDecimal2 = new BigDecimal("0.21");
        BigDecimal subtract = bigDecimal.subtract(bigDecimal2);
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        String percentStr = nf.format(subtract);
        System.out.println(percentStr);
    }

    public String getPercentage(BigDecimal subtract) {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        return nf.format(subtract);
    }

    @Test
    public void test20230630() {
//        BigDecimal subtract = new BigDecimal(0.4370).subtract(new BigDecimal(0.4370));
//        System.out.println(subtract.compareTo(BigDecimal.ZERO)==0);
//        BigDecimal bigDecimal = subtract.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_CEILING);
//        String change=subtract.compareTo(BigDecimal.ZERO)==0?"0":bigDecimal.toPlainString();
//        System.out.println("===>"+change+"%");
        // 年份
        // 该年多少周

        // 将年份和周数转换为日期类型
//        int validWeek = Math.min(27, 52);
//        System.out.println(validWeek);
//
//        LocalDate localDateByCategory = getLocalDateByCategory(2023, 27, 1L);
//        System.out.println(localDateByCategory);
//        LocalDate localDate = getLocalDateByCategory(2023, 27, 7L);
//        System.out.println(localDate);
        String s = "{\"showChildren\":true,\"name\":\"tabView\",\"class\":\"box-50\",\"articleId\":\"22,23,25\",\"showType\":\"33\"}";
        Map<String, Object> map = JSONUtil.parseObj(s, true);
        System.out.println(JSONObject.toJSONString(map));
        System.out.println(map.get("articleId")); // 输出：2023-12-31
        String articleId = map.get("articleId").toString();
        if (articleId.contains(",")) {
            String[] split = articleId.split(",");
            List<String> strings = new ArrayList<>(Arrays.asList(split));
            System.out.println(JSONObject.toJSONString(strings));
        }
    }

    public static LocalDate getLocalDateByCategory(Integer year, Integer week, long newValue) {
        WeekFields weekFields = WeekFields.of(DayOfWeek.SATURDAY, 3);
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.withYear(year).with(weekFields.weekOfYear(), week);
        return localDate.with(weekFields.dayOfWeek(), newValue);
    }

    @Test
    public void testverCode() {
        String verCode = "BAIINFO";
        if ("baiinfo".equalsIgnoreCase(verCode)) {
            System.out.println("校验成功");
        }
        String code = "9H8ie";
        String s = code.toLowerCase();
        System.out.println(s);


        BigDecimal bigDecimal1 = new BigDecimal("12.00");
        BigDecimal bigDecimal2 = new BigDecimal("6");
        BigDecimal result = bigDecimal2.divide(bigDecimal1, 2, BigDecimal.ROUND_HALF_UP);
        System.out.println(result.stripTrailingZeros());
        System.out.println(result.stripTrailingZeros().toPlainString());
    }

    @Test
    public void testzhengze() {
        String input = "<p id=\"w-e-element-5\" data-slate-node=\"element\" data-slate-fragment=\"JTVCJTdCJTIydHlwZSUyMiUzQSUyMnBhcmFncmFwaCUyMiUyQyUyMmNoaWxkcmVuJTIyJTNBJTVCJTdCJTIydGV4dCUyMiUzQSUyMiUyMiU3RCU1RCU3RCUyQyU3QiUyMnR5cGUlMjIlM0ElMjJwYXJhZ3JhcGglMjIlMkMlMjJjaGlsZHJlbiUyMiUzQSU1QiU3QiUyMnRleHQlMjIlM0ElMjIlMjIlN0QlNUQlN0QlMkMlN0IlMjJ0eXBlJTIyJTNBJTIycGFyYWdyYXBoJTIyJTJDJTIyY2hpbGRyZW4lMjIlM0ElNUIlN0IlMjJ0ZXh0JTIyJTNBJTIyJTIyJTdEJTVEJTdEJTJDJTdCJTIydHlwZSUyMiUzQSUyMnBhcmFncmFwaCUyMiUyQyUyMmNoaWxkcmVuJTIyJTNBJTVCJTdCJTIydGV4dCUyMiUzQSUyMiUyMiU3RCUyQyU3QiUyMnR5cGUlMjIlM0ElMjJpbWFnZSUyMiUyQyUyMnNyYyUyMiUzQSUyMiU1Q3IlNUNuaHR0cCUzQSUyRiUyRnN0YXRpYy5iYWlpbmZvLmNuJTJGaW1hZ2UlMkYyMDIzMDYyMCUyRjIwMjMwNjIwMTc0MTE3XzYzNTIucG5nJTVDciU1Q24lMjIlMkMlMjJhbHQlMjIlM0ElMjIlMjIlMkMlMjJocmVmJTIyJTNBJTIyJTIyJTJDJTIyc3R5bGUlMjIlM0ElN0IlMjJ3aWR0aCUyMiUzQSUyMiUyMiUyQyUyMmhlaWdodCUyMiUzQSUyMiUyMiU3RCUyQyUyMmNoaWxkcmVuJTIyJTNBJTVCJTdCJTIydGV4dCUyMiUzQSUyMiUyMiU3RCU1RCU3RCUyQyU3QiUyMnRleHQlMjIlM0ElMjIlMjIlN0QlNUQlN0QlNUQ=\">\n" +
                "</p>\n" +
                "<p id=\"w-e-element-7\" data-slate-node=\"element\">\n" +
                "</p>\n" +
                "<p id=\"w-e-element-11\" data-slate-node=\"element\">\n" +
                "</p>\n" +
                "<p id=\"w-e-element-3\" data-slate-node=\"element\">\n" +
                "</p>\n" +
                "<p>\n" +
                "<br>\n" +
                "</p>\n" +
                "<p>\n" +
                "&nbsp;\n" +
                "</p>\n" +
                "<p>\n" +
                "<br>\n" +
                "</p>\n" +
                "<div id=\"w-e-image-container-14\" class=\"w-e-image-container\">\n" +
                "<img style=\"max-width: 100%\" src=\"http://static.baiinfo.com/image&#13;&#10;/20230629/20230629153231_1614.png\" alt=\"\">\n" +
                "</div>\n" +
                "<p>\n" +
                "<img style=\"max-width: 100%\" src=\"http://static.baiinfo.com/image&#13;&#10;/20230629/20230629153231_1614.png\" alt=\"\">\n" +
                "</p>\n" +
                "<p>\n" +
                "</p>\n" +
                "<p>\n" +
                "</p>";
//        String regex = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(input);
//        List<String> srcList = new ArrayList<>();
//        while (matcher.find()) {
//            String srcValue = matcher.group(1);
//            srcList.add(srcValue);
//        }
//        System.out.println("1====>"+JSONObject.toJSONString(srcList));

        Pattern pua = Pattern.compile("src=\"(.*?)\"");
        Matcher mat = pua.matcher(input);

        List<String> srcList = new ArrayList<>();
        while (mat.find()) {
            String src = mat.group(1);
            srcList.add(src);
        }
        System.out.println("2====>" + JSONObject.toJSONString(srcList));

        String[] strArr = new String[]{"&nbsp;", "&emsp;", "&#09;", "<br>", "<br/>", "&#13;&#10;"};
        List<String> result = srcList.stream()
                .map(item -> {
                    for (String i : strArr) {
                        item = item.replaceAll(Pattern.quote(i), "");
                    }
                    return item;
                })
                .collect(Collectors.toList());
        System.out.println(JSONObject.toJSONString(result));
    }

    @Test
    public void getDate() {
//        Date parse = DateUtil.parse("2023-01-05");
//        String dateTime = DateUtil.format(DateUtil.beginOfDay(DateUtil.offsetDay(parse, -30)), "yyyy-MM-dd");
//        System.out.println(dateTime);
//        String s1="2023-07-06";
//        String s2="2023-07-05";
//        String s3="2023-07-07";
//        String s4="2023-07-08";
//        List<String> list=new ArrayList<>();
//        list.add(s1);
//        list.add(s2);
//        list.add(s3);
//        list.add(s4);
//        List<String> collect = list.stream().sorted().collect(Collectors.toList());
//        System.out.println(JSONObject.toJSONString(collect));
//        String name="baiinfo.png";
//        String substring = name.substring(name.lastIndexOf("."));
//        System.out.println(substring);
//        String text="   jaifjaja";
//        text=text.trim();
//        System.out.println(text);
        /**
         *
         */
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("news", "id");
//        Object news = jsonObject.get("news");
//        System.out.println(JSONObject.toJSONString(news));

//        String s = "123";
//        Integer integer = Integer.valueOf(s);
//        System.out.println(integer);
//        String s=",11,";
//        String[] split = s.split(",");
//        for (int i = 0; i < split.length; i++) {
//            System.out.println(split[i]);
//        }
//        System.out.println(JSONObject.toJSONString(split));
//        List<String> strings = Arrays.asList(split);
//        List<String> collect = strings.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
//        System.out.println(JSONObject.toJSONString(collect));
    }
    @Autowired
    private WeatherService weatherService;
    @Test
    public void getXiaopengTianqi(){
        Date date = new Date();
        String format = DateUtil.format(DateUtil.tomorrow(), "yyyy-MM-dd");
        System.out.println(format);
        int year = DateUtil.year(date);
        int month = DateUtil.month(date) + 1;
        int day = DateUtil.dayOfMonth(date);
        LambdaQueryWrapper<WeatherEntity> query = new LambdaQueryWrapper<>();
        query.eq(WeatherEntity::getFxDate,format);
        WeatherEntity one = weatherService.getOne(query);
        System.out.println(JSONObject.toJSONString(one));
        System.out.println(year);
        System.out.println(month);
        System.out.println(day);
        String s ="尊敬的用户您好，今天是"+year+"年"+month+"月"+day+"日,小鹏为您带来明日天气预报,明日"+one.getTextDay()+",最高气温"+one.getTempMax()+"℃,最低气温"+one.getTempMin()+"℃,"+one.getWindDirDay()+",风速"+one.getWindSpeedDay()+"级";
        System.out.println(s);
    }
    @Test
    public void dnf() {
        BigDecimal count = BigDecimal.valueOf(338 + 338 * 0.7);
        System.out.println(count);
        BigDecimal s = count.multiply(BigDecimal.valueOf(1000000 / 184));
        System.out.println(s.setScale(0, BigDecimal.ROUND_UP));
        System.out.println(4000000 * 40 + (10000000) * 8*5);
        int i = 28+24+4+3+8+8+20+3+4+6+6+4+5+3;
        int mingw=42902+350+350+700+350;
        System.out.println("============>>>>"+150000000/1000000*2.00);
        System.out.println("花花：" + i);
        System.out.println("花花：" + mingw);
    }

    @Test
    public void test0718() {
        Integer[] arrs = new Integer[]{2, 3, 1, 2, 11274, 130791, 15270, 15273, 15274, 15275, 15276, 15273, 15274, 15275, 15276, 14584, 15289, 15290, 15291, 16968713, 131798, 11306, 11314, 128714, 12879, 128806, 14276, 14264, 131759, 10138, 11314, 11306, 128806, 14264, 99, 666, 121, 15151, 14264, 131798, 11306, 16709, 16709, 131798, 11306, 11314, 16709, 11306, 131798, 1, 123123, 14264, 11306, 11314, 131798, 2297, 128806, 11306, 131798, 2297, 14264, 14276, 14264, 14276, 14264, 1, 2, 1, 1, 14264, 14276, 14264, 14276, 12, 10, 1, 1, 1, 1, 23, 1, 1, 1, 2, 3, 1, 2, 3, 2, 1, 1, 1, 1, 11314, 131798, 1, 11314, 12879, 131798, 1, 1, 3, 14264, 14264, 14276, 131798, 9999, 2212, 14264, 131798, 5817, 14449, 14450, 14452, 5817, 14449, 14450, 14584, 14625, 14584, 14584, 14584, 14625, 14268, 14270, 14388, 14448, 2297, 2212, 130427, 2365, 11318, 13841, 11314, 11318, 13841, 11314, 14625, 14264, 14625, 131798, 131798, 12847, 2212, 2212, 131798};
        List<Integer> arrayList = new ArrayList<>(Arrays.asList(arrs));
        List<Integer> collect = arrayList.stream().distinct().collect(Collectors.toList());
        System.out.println(JSONObject.toJSONString(collect));
        System.out.println(JSONObject.toJSONString(arrs.length));
        System.out.println(JSONObject.toJSONString(collect.size()));
    }

    @Test
    public void testMap() {
        //判断字符串是否为数字类型
        System.out.println(isNumber("73.278"));
        System.out.println(isNumber("73,278"));
        Date date = new Date();
        String startTime = DateUtil.format(DateUtil.offsetMonth(DateUtil.beginOfMonth(DateUtil.date()), -6), "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.format(DateUtil.endOfMonth(DateUtil.date()), "yyyy-MM-dd HH:mm:ss");
        System.out.println(startTime);
        System.out.println(endTime);
    }
    boolean isNumber(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        String reg = "^-?[0-9]+(\\.[0-9]+)?$";
        return str.matches(reg);

    }

    @Value("${wechatConfig.appSecret}")
    private List<Integer> ids;

    @Test
    public void testMap1() {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        ImageTransform imageTransform = new Base64ImageTransform();
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager, imageTransform).init(true);
        /*
                生成滑块验证码图片, 可选项
                SLIDER (滑块验证码)
                ROTATE (旋转验证码)
                CONCAT (滑动还原验证码)
                WORD_IMAGE_CLICK (文字点选验证码)

                更多验证码支持 详见 cloud.tianai.captcha.common.constant.CaptchaTypeConstant
         */
        ImageCaptchaInfo imageCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.SLIDER);
        System.out.println(imageCaptchaInfo);
        // 负责计算一些数据存到缓存中，用于校验使用
        // ImageCaptchaValidator负责校验用户滑动滑块是否正确和生成滑块的一些校验数据; 比如滑块到凹槽的百分比值
        ImageCaptchaValidator imageCaptchaValidator = new BasicCaptchaTrackValidator();
        // 这个map数据应该存到缓存中，校验的时候需要用到该数据
        Map<String, Object> map = imageCaptchaValidator.generateImageCaptchaValidData(imageCaptchaInfo);
        System.out.println(JSONObject.toJSONString(map));
    }
    @Test
    public void testDate2122(){
//        Calendar c1 = Calendar.getInstance();
//        c1.add(Calendar.MONTH, -8);
//        String sixMonthsAgo = new SimpleDateFormat("yyyy-MM-01").format(c1.getTime());
//        // 获取6个月前的日期
//        Calendar c2 = Calendar.getInstance();
//        c2.add(Calendar.MONTH, -2);
//        String currentDate = new SimpleDateFormat("yyyy-MM-01").format(c2.getTime());
//        System.out.println(sixMonthsAgo);
//        System.out.println(currentDate);
//       List<User> users = new ArrayList<User>();
//        for (int i = 0; i < 100; i++) {
//            User user = new User();
//            user.setId(i);
//            user.setName("小"+i);
//            if(i%2>0) {
//                user.setOpenDate(DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -100-i)));
//            }else{
//                user.setOpenDate(DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -100-i-1)));
//            }
//            users.add(user);
//        }
//        List<User> users = IntStream.range(0, 100)
//                .mapToObj(i -> {
//                    User user = new User();
//                    user.setId(i);
//                    user.setName("小" + i);
//                    user.setOpenDate(DateUtil.format(DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -100 - i - (i % 2 > 0 ? 0 : 1))),"yyyy-MM-dd"));
//                    user.setEndDate(DateUtil.format(DateUtil.endOfDay(DateUtil.offsetDay(DateUtil.date(), -100 - i - (i % 2 > 0 ? 0 : 1))),"yyyy-MM-dd"));
//                    return user;
//                }).collect(Collectors.toList());
//        List<User> users1 = new ArrayList<>();
//        List<List<User>> partition = ListUtil.partition(users1, 200);
//        System.out.println("==========="+JSONObject.toJSONString(partition));
//        if(CollectionUtil.isNotEmpty(partition)) {
//            ForkJoinPool forkJoinPool = new ForkJoinPool(Math.min(partition.size(), 10));
//            forkJoinPool.submit(() -> partition.parallelStream().forEach(e -> {
//                System.out.println(JSONObject.toJSONString(e));
//            })).join();
//            forkJoinPool.shutdown();
//        }
    }
}
