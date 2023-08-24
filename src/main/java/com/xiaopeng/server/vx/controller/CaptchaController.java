package com.xiaopeng.server.vx.controller;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant;
import cloud.tianai.captcha.generator.common.model.dto.ImageCaptchaInfo;
import cloud.tianai.captcha.generator.impl.MultiImageCaptchaGenerator;
import cloud.tianai.captcha.generator.impl.transform.Base64ImageTransform;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import cloud.tianai.captcha.resource.impl.provider.URLResourceProvider;
import cloud.tianai.captcha.spring.annotation.Captcha;
import cloud.tianai.captcha.spring.request.CaptchaRequest;
import cloud.tianai.captcha.validator.ImageCaptchaValidator;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.impl.BasicCaptchaTrackValidator;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wf.captcha.SpecCaptcha;
import com.xiaopeng.server.app.bean.common.ResultBean;
import com.xiaopeng.server.vx.entity.ImageCaptchaTrackDto;
import com.xiaopeng.server.vx.vo.ImageCaptchaInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName: Captcha
 * @Author: BUG-WZP
 * @Since: 2023/7/27
 * @Remark: 验证码
 */
@RestController
@RequestMapping("/Captcha/")
@Slf4j
@Api(tags = "验证码")
public class CaptchaController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * TODO hutool类生成图形验证码demo
     * 验证完成验证码后需要删除验证码
     * redisTemplate.delete("xiaopeng:server:" + verCode);
     */
    @GetMapping("/createCaptcha")
    @ApiOperation("hutool类生成图形验证码")
    public ResultBean createCaptcha(HttpServletRequest request, HttpServletResponse response) {
        //宽 、高、 字数和干扰线条数
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(130, 48, 5, 0);
        lineCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));
        String verCode = lineCaptcha.getCode();
        log.info("验证码=====>{}", verCode);
        try {
            redisTemplate.opsForValue().set("xiaopeng:server:" + verCode, verCode, 10L, TimeUnit.MINUTES);
            lineCaptcha.write(response.getOutputStream());
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBean.fail("fail", e.getMessage());
        }
        return ResultBean.of(200, "success", null);
    }

    public static final int TYPE_ONLY_CHAR = 3;

    /**
     * todo 文件流二进制验证码demo
     *
     * @return
     */
    @GetMapping("/captcha")
    @ApiOperation("文件流二进制验证码")
    public ResultBean captcha() {
//        EasySpecCaptcha specCaptcha = new EasySpecCaptcha(130, 48, 5);
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(CaptchaController.TYPE_ONLY_CHAR);
        String verCode = specCaptcha.text().toLowerCase();
        String key = UUID.randomUUID().toString();
        log.info("AuthController: captcha  ====>" + verCode);
        // 存入redis并设置过期时间为30分钟
        redisTemplate.opsForValue().set("key" + key, verCode, 30L, TimeUnit.MINUTES);
        // 将key和base64返回给前端   如果不想要base64的头部data:image/png;base64, 加一个空的参数即可
        return new ResultBean(specCaptcha.toBase64("data:image/png;base64,"));
    }

    @Captcha("SLIDER")
    @PostMapping("/login")
    public String login(@RequestBody ImageCaptchaTrackDto dto) {
        // 进入这个方法就说明已经校验成功了
        return "success";
    }
    public static final Float tolerant=0.02f;
    private static final String externaUrl="http://1.202.26.70:9000/";
    @PostMapping("/huadongCap")
    @ApiOperation("滑动验证码")
    public ImageCaptchaInfoVo getslideCaptcha() {
        ImageCaptchaResourceManager imageCaptchaResourceManager = new DefaultImageCaptchaResourceManager();
        /**
         * 自定义图片的话，需要添加远程资源或者内置资源图片
         */
        ResourceStore resourceStore = imageCaptchaResourceManager.getResourceStore();
        resourceStore.addResource(CaptchaTypeConstant.SLIDER,new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/1.jpg"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER,new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/2.jpg"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER,new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/3.jpg"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER,new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/4.jpg"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER,new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/5.jpg"));
        ResourceMap template1 = new ResourceMap("default1", 1);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/active.png"));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/fixed.png"));
        template1.put(SliderCaptchaConstant.TEMPLATE_MASK_IMAGE_NAME, new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/matrix.png"));
        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template1);

        ResourceMap template2 = new ResourceMap("default2", 2);
        template2.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/active1.png"));
        template2.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/fixed1.png"));
        template2.put(SliderCaptchaConstant.TEMPLATE_MASK_IMAGE_NAME, new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, externaUrl+"baiiinfo-app-service/appCaptchaTemplate/matrix1.png"));
        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template2);
//        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new cloud.tianai.captcha.resource.common.model.dto.Resource(ClassPathResourceProvider.NAME, "tmp/aa.png"));
//         添加远程url图片资源
        //        resourceStore.addResource(CaptchaTypeConstant.SLIDER,new cloud.tianai.captcha.resource.common.model.dto.Resource(URLResourceProvider.NAME, "http://www.xx.com/aa.jpg"));
        /**
         * 传递y轴，隐藏x轴
         */
        ImageCaptchaInfoVo imageCaptchaInfoVo = new ImageCaptchaInfoVo();
        ImageTransform imageTransform = new Base64ImageTransform();
        ImageCaptchaGenerator imageCaptchaGenerator = new MultiImageCaptchaGenerator(imageCaptchaResourceManager, imageTransform).init(false);
        ImageCaptchaInfo imageCaptchaInfo = imageCaptchaGenerator.generateCaptchaImage(CaptchaTypeConstant.SLIDER);
        imageCaptchaInfo.setTolerant(tolerant);
        ImageCaptchaValidator imageCaptchaValidator = new BasicCaptchaTrackValidator();
        JSONObject parse = JSONObject.parseObject(JSONObject.toJSONString(imageCaptchaInfo));
        Integer y = parse.getInteger("y");
        Map<String, Object> map = imageCaptchaValidator.generateImageCaptchaValidData(imageCaptchaInfo);
        map.put("imageX",imageCaptchaInfo.getRandomX());
        map.put("imageY",y);
        BeanUtil.copyProperties(imageCaptchaInfo,imageCaptchaInfoVo);
        String uuid = UUID.randomUUID().toString();
        imageCaptchaInfoVo.setRandomX(uuid);
        redisTemplate.opsForValue().set("xiaopeng-server"  +  tolerant+"-"+ map.get("type")+"-"+ uuid, JSONObject.toJSONString(map), 5L, TimeUnit.MINUTES);
        return imageCaptchaInfoVo;
    }
    @PostMapping("/authPhoneCaptcha")
    @ApiOperation("校验滑动验证码")
    public Boolean authPhoneCaptcha(ImageCaptchaTrackDto dto){
        String uuid = dto.getRandomX();
        String type = dto.getType();
        Float tolerantF = dto.getTolerant();
        String s = redisTemplate.opsForValue().get("xiaopeng-server" + tolerantF +"-"+ type+"-"+ uuid);
        if(StringUtils.isBlank(s)){
            return false;
        }
        Map<String, Object> map = JSONObject.parseObject(s);
        int x = (Integer) map.get("imageX");
        int y = (Integer) map.get("imageY");
        BasicCaptchaTrackValidator sliderCaptchaValidator = new BasicCaptchaTrackValidator();
        ImageCaptchaInfo imageCaptchaInfo = new ImageCaptchaInfo();
        imageCaptchaInfo.setTolerant(tolerant);
        imageCaptchaInfo.setType(dto.getType());
        imageCaptchaInfo.setRandomX(x);
        imageCaptchaInfo.setBackgroundImageWidth(dto.getBackgroundImageWidth());
        imageCaptchaInfo.setBackgroundImageHeight(dto.getBackgroundImageHeight());
        imageCaptchaInfo.setTemplateImageWidth(dto.getTemplateImageWidth());
        imageCaptchaInfo.setBackgroundImageHeight(dto.getBackgroundImageHeight());
        ImageCaptchaValidator imageCaptchaValidator = new BasicCaptchaTrackValidator();
        Map<String, Object> map1 = imageCaptchaValidator.generateImageCaptchaValidData(imageCaptchaInfo);
        Float percentage =Float.parseFloat( map1.get("percentage").toString());
        Float newPercentage =Float.parseFloat( map1.get("percentage").toString());

        int i = x + y - (dto.getTargetX() + dto.getTargetY());
        boolean check1= i <= 4 && i >= -4;
        boolean check2 = sliderCaptchaValidator.checkPercentage(newPercentage, percentage, tolerant);
        redisTemplate.delete("xiaopeng-server"  + tolerantF +"-"+ type+"-"+ uuid);
        return check1&&check2;
    }
}
